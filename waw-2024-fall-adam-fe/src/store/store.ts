import { configureStore } from '@reduxjs/toolkit';
import userReducer from './slices/userSlice';
import borrowedBooksReducer from './slices/borrowedBookSlice';

const store = configureStore({
  reducer: {
    user: userReducer,
    borrowedBooks: borrowedBooksReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;
