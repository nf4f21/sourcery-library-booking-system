import { configureStore } from "@reduxjs/toolkit";
import { storeAuthTokensInCookies } from "../../../auth/cookies";

import axios, { AxiosRequestConfig } from "axios";
import userReducer from "../../../store/slices/userSlice";
import BookDetails from "../../../components/BookDetails/BookDetails";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { Provider } from "react-redux";
import { act, render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import sampleData from "./sampleData.json";
import BookDetailsResult from "../../../models/BookDetailsResult.interface";
import ActiveReservation from "../../../models/ActiveReservation.interface";

const sampleBookDetailsResult: BookDetailsResult = {
  ...sampleData.bookDetailsResult,
  publicationDate: new Date(sampleData.bookDetailsResult.publicationDate),
};
const availabilityResult: boolean = sampleData.availabilityResult;

const activeReservationsResult: ActiveReservation[] = sampleData.activeReservationsResult.map((reservation) => ({
  ...reservation,
  borrowedFrom: new Date(reservation.borrowedFrom),
  returnDate: new Date(reservation.returnDate),
}));

const bookId = sampleBookDetailsResult.bookId;

jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;
mockedAxios.request.mockImplementation((config: AxiosRequestConfig) => {
  if (config.url === `http://localhost:8080/api/v1/books/${bookId}`) {
    return Promise.resolve({ data: sampleBookDetailsResult });
  }
  if (config.url === `http://localhost:8080/api/v1/books/${bookId}/availability`) {
    return Promise.resolve({ data: sampleData.availabilityResult });
  }
  if (config.url === `http://localhost:8080/api/v1/books/${bookId}/active-reservations`) {
    return Promise.resolve({ data: sampleData.activeReservationsResult });
  }
  return Promise.reject(new Error('Unknown endpoint'));
});

describe("BookDetails tests", () => {

  beforeAll(() => {
    global.URL.createObjectURL = jest.fn();
    global.URL.revokeObjectURL = jest.fn();
  });

  beforeEach(async () => {

    storeAuthTokensInCookies({
      token: "",
      refreshToken: "",
    });

    const store = configureStore({
      reducer: {
        user: userReducer,
      },
    });

    await act(async () => {
      render(
        <Provider store={store}>
          <MemoryRouter initialEntries={[`/books/${bookId}`]}>
            <Routes>
              <Route path="/books/:bookId" element={<BookDetails />} />
            </Routes>
          </MemoryRouter>
        </Provider>
      );
    });

  });

  test("Displays book details", async () => {

    await waitFor(() => {

      expect(screen.getByText(sampleBookDetailsResult.title)).toBeInTheDocument();
      expect(screen.getByText(sampleBookDetailsResult.author)).toBeInTheDocument();
      expect(screen.getByText(sampleBookDetailsResult.description)).toBeInTheDocument();

      // details section
      expect(screen.getByText(sampleBookDetailsResult.series)).toBeInTheDocument();
      expect(screen.getByText(sampleBookDetailsResult.publisher)).toBeInTheDocument();
      expect(screen.getByText(sampleBookDetailsResult.isbn)).toBeInTheDocument();
      expect(screen.getByText(sampleBookDetailsResult.editionLanguage)).toBeInTheDocument();
      expect(screen.getByText(sampleBookDetailsResult.category)).toBeInTheDocument();

      if (availabilityResult) {
        expect(screen.getByText("Available")).toBeInTheDocument();
      } else {
        expect(screen.getByText("Currently unavailable")).toBeInTheDocument();
      }

    })
  });

  test("Display active reservations table", async () => {

    await waitFor(() => {
      activeReservationsResult.forEach((reservation) => {
        const employeeHeader = screen.getByText(
          (content, element) => content === "EMPLOYEE"
        );
        expect(employeeHeader).toBeInTheDocument();

        const officeHeader = screen.getByText(
          (content, element) => content === "OFFICE"
        );
        expect(officeHeader).toBeInTheDocument();

        const bookFromHeader = screen.getByText(
          (content, element) => content === "BOOKED FROM"
        );
        expect(bookFromHeader).toBeInTheDocument();

        const returnDateHeader = screen.getByText(
          (content, element) => content === "RETURN DATE"
        );
        expect(returnDateHeader).toBeInTheDocument();
        
        expect(screen.getByText(`${reservation.firstName} ${reservation.lastName}`)).toBeInTheDocument();
        expect(screen.getByText(reservation.officeName)).toBeInTheDocument();
      });
    });
  });

});
