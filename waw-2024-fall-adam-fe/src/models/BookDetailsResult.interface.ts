import BookCopy from "./BookCopy.interface";

interface BookDetailsResult {
    bookId: number;
    coverImage: string; 
    title: string;  
    author: string;
    description: string;
    format: string;
    numberOfPages: number;
    publicationDate: Date;
    publisher: string;
    isbn: string;
    editionLanguage: string;
    series: string;
    category: string;
    bookCopies: BookCopy[];
}
export default BookDetailsResult;
