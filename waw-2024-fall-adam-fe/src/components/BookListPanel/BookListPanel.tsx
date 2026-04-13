import React, { useEffect } from 'react';
import Book from '../../models/Book.interface';
import './BookListPanel.css';
import CoverImage from '../CoverImage/CoverImage';
import { Link } from 'react-router-dom';
import useFetch from '../hooks/UseFetch';
import CircularProgress from '@mui/material/CircularProgress';


const BookListPanel: React.FC = () => {
  // Use useFetch to get the list of books
  const { data: bookList, loading, error, fetchData } = useFetch<Book[]>('/books?pageSize=10&pageNumber=0');

  useEffect(() => {
    // Fetch books when the component mounts
    fetchData();
  }, []); // Ensures fetchData runs only once on component mount

  return (
    <div className="books-list-panel">
      <h1 className='books-list-panel-title'>
        List books
      </h1>
      <div className="books-list-panel-container">
        {loading && <CircularProgress />} 
        {error && <div>Error: {error}</div>}
        {bookList && bookList.map((book: Book) => (
          <div className="book-list-panel-container" key={book.bookId}>
            <div className="book-list-panel-cover-image-container">
              <CoverImage coverImage={book.coverImage} />
            </div>
            <div className="book-list-panel-details-container">
              <div className="book-list-panel-title-and-author">
                <Link className='book-list-panel-title' to={`/books/${book.bookId}`}>
                  {book.title}
                </Link>
                <div className='book-list-panel-author'>
                  {book.author}
                </div>
              </div>
              <div   className={`book-list-panel-status ${book.isAvailable ? "text-green-600" : "text-red-600"}`}>
                {book.isAvailable ? " Available" : "Currently Unavailable"}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default BookListPanel;
