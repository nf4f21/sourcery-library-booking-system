import {
  act,
  fireEvent,
  render,
  RenderResult,
  screen,
  waitFor,
} from "@testing-library/react";
import "@testing-library/jest-dom";
import axios, { AxiosRequestConfig } from "axios";
import ReservationPanel from "../../../../components/BookDetails/ReservationPanel/ReservationPanel";
import BookDetailsResult from "../../../../models/BookDetailsResult.interface";
import sampleData from "../sampleData.json";
import OfficeDetailedForBook from "../../../../models/OfficeDetailedForBook";
import { MemoryRouter } from "react-router-dom";
import { storeAuthTokensInCookies } from "../../../../auth/cookies";

const sampleBookDetailsResult: BookDetailsResult = {
  ...sampleData.bookDetailsResult,
  publicationDate: new Date(sampleData.bookDetailsResult.publicationDate),
};

const bookCopies: OfficeDetailedForBook[] = sampleData.offices;

jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;
mockedAxios.request.mockImplementation((config: AxiosRequestConfig) => {
  const bookId = config.url?.split("/")[3];
  if (bookId) {
    return Promise.resolve({ data: bookCopies });
  }
  return Promise.reject(
    new Error(`Unmocked axios request for URL: ${config.url}`)
  );
});

describe("ReservationPanel tests", () => {
  async function renderReservationPanel(): Promise<RenderResult> {
    // this is needed to avoid redirects, because useFetch first checks for tokens, and then makes a request
    storeAuthTokensInCookies({
      token: '',
      refreshToken: '',
    });
    const renderResult = render(
      <MemoryRouter initialEntries={["/offices/book/1"]}>
        <ReservationPanel bookDetails={sampleBookDetailsResult} />{" "}
      </MemoryRouter>
    );
    // wait for the data to load
    await waitFor(() => renderResult.getByTestId("offices-list"));
    return renderResult;
  }

  test("renders ReservationPanel component with expected header text and button", async () => {
    await renderReservationPanel();

    const headerElement = screen.getByText(
      (content, element) => content === "Borrow from"
    );
    const borrowButtonElement = screen.getByText(
      (content, element) =>
        content === "Borrow" && element?.tagName.toLowerCase() === "button"
    );
    expect(headerElement).toBeInTheDocument();
    expect(borrowButtonElement).toBeInTheDocument();
  });

  test("ReservationModal should open when clicking on the borrow button", async () => {
    // this is needed to prevent TypeErrors
    global.URL.createObjectURL = jest.fn();
    global.URL.revokeObjectURL = jest.fn();

    const { getByTestId } = await renderReservationPanel();

    const borrowButtonElement = screen.getByText(
      (content, element) =>
        content === "Borrow" && element?.tagName.toLowerCase() === "button"
    );
    // open the modal
    act(() => {
      fireEvent.click(borrowButtonElement);
    });
    const modalHeader = await waitFor(() => getByTestId("modal-header"));
    expect(modalHeader).toBeInTheDocument();
  });
});
