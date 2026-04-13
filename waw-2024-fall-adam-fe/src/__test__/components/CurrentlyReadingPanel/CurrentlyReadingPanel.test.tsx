import {
  render,
  screen,
  waitFor,
  act,
  fireEvent,
} from "@testing-library/react";
import CurrentlyReadingPanel from "../../../components/CurrentlyReadingPanel/CurrentlyReadingPanel";
import axios from "axios";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import borrowedBooksReducer from "../../../store/slices/borrowedBookSlice";
import { configureStore } from "@reduxjs/toolkit";
import userReducer from "../../../store/slices/userSlice";
import { setBorrowedBooks } from "../../../store/slices/borrowedBookSlice";
import { storeAuthTokensInCookies } from "../../../auth/cookies";
import { MemoryRouter } from "react-router-dom";

jest.mock("axios");
global.URL.createObjectURL = jest.fn();
global.URL.revokeObjectURL = jest.fn();

describe("Currently reading panel", () => {
  const mockCurrentlyReadingBooks = [
    {
      borrowedId: 1,
      title: "Book One",
      author: "Author One",
      coverImage:
        "iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAABhGlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9TRSktgnYo4pChOlkQFdFNq1CECqFWaNXB5NIvaNKQpLg4Cq4FBz8Wqw4uzro6uAqC4AeIs4OToouU+L+m0CLGg+N+vLv3uHsHCPUy06yuMUDTbTOViIuZ7KrY84oAIuhHCDMys4w5SUrCc3zdw8fXuxjP8j735wipOYsBPpF4lhmmTbxBPLVpG5z3icOsKKvE58SjJl2Q+JHristvnAtNFnhm2Eyn5onDxGKhg5UOZkVTI54kjqqaTvlCxmWV8xZnrVxlrXvyFwZz+soy12kOIYFFLEGCCAVVlFCGjRitOikWUrQf9/APNv0SuRRylcDIsYAKNMhNP/gf/O7Wyk+Mu0nBOND94jgfw0DPLtCoOc73seM0TgD/M3Clt/2VOjD9SXqtrUWPgL5t4OK6rSl7wOUOEHkyZFNuSn6aQj4PvJ/RN2WBgVsgsOb21trH6QOQpq6SN8DBITBSoOx1j3f3dvb275lWfz+lD3K7ulqJHAAAAAlwSFlzAAAuIwAALiMBeKU/dgAAAAd0SU1FB+gKGBIQBLvT9cgAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAAFUlEQVQI1wXBAQEAAACAEP9PF1CpMCnkBftjnTYAAAAAAElFTkSuQmCC",
      returnDate: "2024-12-11"
    },
    {
      borrowedId: 2,
      title: "Book Two",
      author: "Author Two",
      coverImage:
        "iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAABhGlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9TRSktgnYo4pChOlkQFdFNq1CECqFWaNXB5NIvaNKQpLg4Cq4FBz8Wqw4uzro6uAqC4AeIs4OToouU+L+m0CLGg+N+vLv3uHsHCPUy06yuMUDTbTOViIuZ7KrY84oAIuhHCDMys4w5SUrCc3zdw8fXuxjP8j735wipOYsBPpF4lhmmTbxBPLVpG5z3icOsKKvE58SjJl2Q+JHristvnAtNFnhm2Eyn5onDxGKhg5UOZkVTI54kjqqaTvlCxmWV8xZnrVxlrXvyFwZz+soy12kOIYFFLEGCCAVVlFCGjRitOikWUrQf9/APNv0SuRRylcDIsYAKNMhNP/gf/O7Wyk+Mu0nBOND94jgfw0DPLtCoOc73seM0TgD/M3Clt/2VOjD9SXqtrUWPgL5t4OK6rSl7wOUOEHkyZFNuSn6aQj4PvJ/RN2WBgVsgsOb21trH6QOQpq6SN8DBITBSoOx1j3f3dvb275lWfz+lD3K7ulqJHAAAAAlwSFlzAAAuIwAALiMBeKU/dgAAAAd0SU1FB+gKGBIQBLvT9cgAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAAFUlEQVQI1wXBAQEAAACAEP9PF1CpMCnkBftjnTYAAAAAAElFTkSuQmCC",
      returnDate: "2024-12-15"
    },
  ];

  beforeEach(async () => {
    storeAuthTokensInCookies({
      token: "",
      refreshToken: "",
    });

    (axios.get as jest.Mock).mockResolvedValueOnce({
      data: mockCurrentlyReadingBooks,
    });
    const store = configureStore({
      reducer: {
        user: userReducer,
        borrowedBooks: borrowedBooksReducer,
      },
    });

    store.dispatch(setBorrowedBooks(mockCurrentlyReadingBooks));

    await act(async () => {
      render(
        <Provider store={store}>
          <MemoryRouter>
            <CurrentlyReadingPanel />
          </MemoryRouter>
        </Provider>
      );
    });
  });

  test("Render the component and displays currently reading books", async () => {
    await waitFor(() => {
      expect(screen.getByText(/Currently reading/i)).toBeInTheDocument();
      expect(screen.getByText("Book One")).toBeInTheDocument();
      expect(screen.getByText("Author One")).toBeInTheDocument();
      expect(screen.getByText(/2024-12-11/i)).toBeInTheDocument();
      expect(screen.getByText("Book Two")).toBeInTheDocument();
      expect(screen.getByText("Author Two")).toBeInTheDocument();
      expect(screen.getByText(/2024-12-15/i)).toBeInTheDocument();
    });

    const editButtons = await screen.findAllByTestId(
      "currently-reading-panel-edit-button"
    );
    const returnButtons = await screen.findAllByTestId(
      "currently-reading-panel-return-button"
    );
    expect(editButtons[0]).toBeInTheDocument();
    expect(returnButtons[0]).toBeInTheDocument();
    expect(editButtons[1]).toBeInTheDocument();
    expect(returnButtons[1]).toBeInTheDocument();
  });

  test("EditReturnDate dialog should open when clicked on the edit button", async () => {
    const editButtons = await screen.findAllByTestId(
      "currently-reading-panel-edit-button"
    );
    expect(editButtons[0]).toBeInTheDocument();
    const editButtonElement = editButtons[0];

    await act(async () => {
      fireEvent.click(editButtonElement);
    });
    const editReturnDateDialog = await waitFor(
      () =>
        screen.getByText("Edit Return Date") &&
        screen.getByText("Please enter the new return date for this book.")
    );

    expect(editReturnDateDialog).toBeInTheDocument();
  });
});
