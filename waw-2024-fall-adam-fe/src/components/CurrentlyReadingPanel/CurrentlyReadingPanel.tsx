import "./CurrentlyReadingPanel.css";
import { useSelector, useDispatch } from "react-redux";
import { RootState } from "../../store/store";
import {
  setBorrowedBooks,
  editReturnDate,
  returnBook,
} from "../../store/slices/borrowedBookSlice";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
} from "@mui/material";
import CoverImage from '../CoverImage/CoverImage';
import { useState, useEffect } from "react";
import useFetch from "../hooks/UseFetch";
import BorrowedBook from "../../models/BorrowedBookInCurrentlyReadingPanel.interface";

const CurrentlyReadingPanel: React.FC = () => {
  const dispatch = useDispatch();

  const { data, error, fetchData } = useFetch<BorrowedBook[]>(
    "/borrowed-books",
    {
      params: {
        status: "BORROWED",
      },
    }
  );

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (data) {
      dispatch(setBorrowedBooks(data));
    }
  }, [data, dispatch]);

  const borrowedBooks = useSelector(
    (state: RootState) => state.borrowedBooks.books
  );

  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [selectedBookId, setSelectedBookId] = useState<number | null>(null);
  const [newReturnDate, setNewReturnDate] = useState<string>("");

  const handleEditClick = (bookId: number) => {
    setSelectedBookId(bookId);
    setEditDialogOpen(true);
  };

  const handleDialogClose = () => {
    setEditDialogOpen(false);
    setSelectedBookId(null);
    setNewReturnDate("");
  };

  const handleSaveReturnDate = () => {
    if (selectedBookId !== null) {
      dispatch(
        editReturnDate({
          borrowedId: selectedBookId,
          returnDate: newReturnDate,
        })
      );
    }
    handleDialogClose();
  };

  return borrowedBooks && borrowedBooks.length > 0 ? (
    <div className="currently-reading-panel">
      <div className="currently-reading-panel-title">Currently reading</div>
      <div className="currently-reading-panel-books-container">
        {error && <div>Error: {error}</div>}
        {borrowedBooks.map((book) => (
          <div
            className="currently-reading-panel-book-container"
            key={book.borrowedId}
          >
            <div className="currently-reading-panel-book-info">
              <div className="currently-reading-panel-cover-image-container">
              <CoverImage coverImage={book.coverImage} />
               
              </div>
              <div className="currently-reading-panel-book-details-container">
                <div className="currently-reading-panel-title-and-author">
                  <div className="currently-reading-panel-book-title">
                    {book.title}
                  </div>
                  <div className="currently-reading-panel-book-author">
                    {book.author}
                  </div>
                </div>
                <div className="currently-reading-panel-book-return-date">
                  Return date: {book.returnDate}
                </div>
              </div>
            </div>
            <div className="currently-reading-panel-book-button-container">
              <button
                className="currently-reading-panel-book-edit"
                onClick={() => handleEditClick(book.borrowedId)}
                data-testid="currently-reading-panel-edit-button"
              >
                Edit
              </button>
              <button
                className="currently-reading-panel-book-return"
                data-testid="currently-reading-panel-return-button"
              >
                Return
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Edit Return Date Dialog */}
      <Dialog open={editDialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Edit Return Date</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Please enter the new return date for this book.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="returnDate"
            label="New Return Date"
            type="date"
            fullWidth
            value={newReturnDate}
            onChange={(e) => setNewReturnDate(e.target.value)}
            InputLabelProps={{
              shrink: true,
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="secondary">
            Cancel
          </Button>
          <Button onClick={handleSaveReturnDate} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  ) : null;
};

export default CurrentlyReadingPanel;
