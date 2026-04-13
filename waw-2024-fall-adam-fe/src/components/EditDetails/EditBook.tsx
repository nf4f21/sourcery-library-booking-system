import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import BookForm, { BookFormFields } from '../BookForm/BookFormBase';
import { useSelector } from 'react-redux';
import { RootState } from '../../store/store';
import { Buffer } from 'buffer';
import useFetch from '../hooks/UseFetch';
import Book from '../../models/Book.interface';

const EditBook = () => {
	const { bookId } = useParams<{ bookId: string }>();
	const [initialData, setInitialData] = useState<Partial<BookFormFields>>();
	const navigate = useNavigate();
	const isAdmin = useSelector((state: RootState) => state.user);

	const { data: book, fetchData: getBook } = useFetch<Book>(`/books/${bookId}`);

	const { data: editedBook, fetchData: editBook } = useFetch<Book>(`/books/${bookId}`, {
		method: 'PUT',
	  });

	useEffect(() => {
		if (!isAdmin) {
			navigate(`/unauthorized`);
		}
	}, [isAdmin, navigate]);


	useEffect(() => {
		getBook();
	}, []); 

	useEffect(() => {
		if(book?.coverImage) {
			
			const imageData = Buffer.from(book.coverImage, 'base64');
			const blob = new Blob([imageData]);
			setInitialData({...book, coverImage: blob});
		}

	}, [book]);

	const handleSubmit = (data: BookFormFields) => {
		const formData = new FormData();

		Object.keys(data).forEach((key) => {
			const typedKey = key as keyof BookFormFields;
			if (typedKey === 'newBookCopies') {
				const newBookCopies = data[typedKey];
				formData.append(`newBookCopies`, JSON.stringify(newBookCopies));
			} else {
				formData.append(typedKey, data[typedKey] as any);
			}
		});
		editBook(formData);
		navigate(`/books/${bookId}`);
	};

	if (!initialData) {
		return <div>Loading...</div>;
	}

	return <BookForm initialData={initialData} onSubmit={handleSubmit} />;
};

export default EditBook;
