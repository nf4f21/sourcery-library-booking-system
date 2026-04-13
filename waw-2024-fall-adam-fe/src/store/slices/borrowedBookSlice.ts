import {createSlice, PayloadAction} from '@reduxjs/toolkit';
import BorrowedBook from '../../models/BorrowedBookInCurrentlyReadingPanel.interface';

interface BorrowedBooksState{
    books: BorrowedBook[];
}

const initialState: BorrowedBooksState = {
    books: [],
};


  const borrowedBooksSlice = createSlice({
    name: 'borrowedBooks',
    initialState,
    reducers: {
      setBorrowedBooks: (state: { books: any; }, action: PayloadAction<BorrowedBook[]>) => {
        state.books = action.payload;
      },
      editReturnDate: (state: { books: any[]; }, action: PayloadAction<{ borrowedId: number; returnDate: string }>) => {
        const book = state.books.find((book: { borrowedId: any; }) => book.borrowedId === action.payload.borrowedId);
        if (book) {
          book.returnDate = action.payload.returnDate;
        }
      },
      returnBook: (state: { books: any[]; }, action: PayloadAction<number>) => {
        state.books = state.books.filter((book: { borrowedId: any; }) => book.borrowedId !== action.payload);
      },
    },
  });
  
  export const { setBorrowedBooks, editReturnDate, returnBook } = borrowedBooksSlice.actions;
  export default borrowedBooksSlice.reducer;