import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import RegisterView from "../components/RegisterView/RegisterView";
import axios from "axios";
import { MemoryRouter } from "react-router-dom";
import "@testing-library/jest-dom";
import { flushPromises } from "./helpers/flushPromises";

jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe("onRegister function", () => {
  test("renders the registration form correctly", () => {
    render(
      <MemoryRouter>
        <RegisterView />
      </MemoryRouter>
    );

    expect(screen.getByText(/Register New User/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/First Name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Last Name/i)).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText(/name@company.com/i)
    ).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/XXXXXXXXXXX/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Password")).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Repeat Password/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Office/i)).toBeInTheDocument();
    expect(screen.getByText(/Sign up/i)).toBeInTheDocument();
  });

  test("should successful message after registration", async () => {
    mockedAxios.post.mockResolvedValue({
      data: {
        userId: 12,
        firstName: "John",
        lastName: "Doe",
        email: "john.doe@example.com",
        phoneNumber: "+48147258369",
        defaultOfficeName: "London",
      },
    });

    render(
      <MemoryRouter initialEntries={["/register"]}>
        <RegisterView />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText("First name"), {
      target: { value: "John" },
    });
    fireEvent.change(screen.getByPlaceholderText("Last name"), {
      target: { value: "Doe" },
    });
    fireEvent.change(screen.getByPlaceholderText("name@company.com"), {
      target: { value: "john.doe@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText("+XXXXXXXXXXX"), {
      target: { value: "+48147258369" },
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "Password123!" },
    });
    fireEvent.change(screen.getByPlaceholderText("Repeat password"), {
      target: { value: "Password123!" },
    });
    fireEvent.change(screen.getByTestId("office-select"), {
      target: { getByTestId: "London" },
    });

    fireEvent.click(screen.getByText("Sign up"));

    await flushPromises();
    await waitFor(() => {
      screen.findByText("User registered");
    });
  });
});
