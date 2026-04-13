import BookForm, { BookFormFields } from '../BookForm/BookFormBase';
import useFetch from '../hooks/UseFetch';
import Book from '../../models/Book.interface';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

const RegisterBook = () => {
	const options = { method: 'POST' }
	const { data: newBookData, loading, error, fetchData } = useFetch<Book>(`/books`, options);
	const navigate = useNavigate();

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
		fetchData(formData);
	};

	useEffect(() => {
		if (newBookData?.bookId) {
			navigate(`/books/${newBookData?.bookId}`);
		}
	}, [newBookData]);

	return <BookForm onSubmit={handleSubmit}  />;
};

export default RegisterBook;