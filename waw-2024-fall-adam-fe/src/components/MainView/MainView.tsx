import Nav from "../Nav/Nav";
import UserPanel from "../UserPanel/UserPanel";
import BookListPanel from "../BookListPanel/BookListPanel";
import CurrentReadingPanel from "../CurrentlyReadingPanel/CurrentlyReadingPanel";
import "./MainView.css";
import { Route, Routes } from "react-router-dom";
import BookDetails from "../BookDetails/BookDetails";
import Unauthorized from "../UnauthorizedView/Unauthorized";
import EditBook from "../EditDetails/EditBook";
import ReservationsView from "../ReservationsView/ReservationsView";
import RegisterBook from "../RegisterNewBook/RegisterBook";

function MainView(): JSX.Element {
  return (
    <div className="main-view-main-container">
      <div className="main-view-left-column">
        <UserPanel />
      </div>
      <div className="main-view-right-column">
        <div className="main-view-nav-class">
          <Nav />
        </div>
        <div className="main-view-content-container">
          <Routes>
            <Route
              path="/"
              element={
                <div className="main-view-content-books">
                  <div className="book-list-container">
                    <BookListPanel />
                  </div>
                  <div className="reading-list-container">
                    <CurrentReadingPanel />
                  </div>
                </div>
              }
            />
						<Route path='/books/:bookId' element={<BookDetails />} />
						<Route path='/reservations' element={<ReservationsView />} />
						<Route path='/unauthorized' element={<Unauthorized />} />
						<Route path='/books/:bookId/edit' element={<EditBook />} />
						<Route path='/books/create' element={<RegisterBook />} />
          </Routes>
        </div>
      </div>
    </div>
  );
}

export default MainView;
