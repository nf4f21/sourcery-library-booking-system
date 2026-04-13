import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import BookDetailsResult from "../../models/BookDetailsResult.interface";

let initialState: BookDetailsResult = {
    bookId: 0,
    coverImage: '',
    title: '', 
    author: '',
    description: '',
    format: '',
    numberOfPages: 0,
    publicationDate: new Date(),
    publisher: '',
    isbn: '',
    editionLanguage: '',
    series: '',
    category: '',
    bookCopies: []
};


const bookDetailsSlice = createSlice({
name: 'bookDetails',
initialState,
reducers: {
    setBookDetails: (state: BookDetailsResult, action: PayloadAction<BookDetailsResult>) => {
    state = action.payload;
    }
},
});
  
  export const { setBookDetails } = bookDetailsSlice.actions;
  export default bookDetailsSlice.reducer;