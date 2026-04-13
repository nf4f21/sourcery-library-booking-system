import { configureStore } from '@reduxjs/toolkit';
import userReducer from '../../store/slices/userSlice';
import borrowedBooksReducer from '../../store/slices/borrowedBookSlice';

function createTestStore(preloadedState = {}) {
  return configureStore({
    reducer: {
      user: userReducer,
      borrowedBooks: borrowedBooksReducer,
    },
    preloadedState,
  });
}

export default createTestStore;
