import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import '@testing-library/jest-dom';
import ReservationsView from '../../../components/ReservationsView/ReservationsView';
import axios, { AxiosRequestConfig } from 'axios';

jest.mock('axios');

// this is needed to prevent TypeErrors
global.URL.createObjectURL = jest.fn();
global.URL.revokeObjectURL = jest.fn();

describe('ReservationsView', () => {
	const mockBorrowedBooks = [
		{
			borrowedId: 1,
			bookCopyId: 5,
			title: 'Book One',
			author: 'Author One',
			coverImage:
				'iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAABhGlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9TRSktgnYo4pChOlkQFdFNq1CECqFWaNXB5NIvaNKQpLg4Cq4FBz8Wqw4uzro6uAqC4AeIs4OToouU+L+m0CLGg+N+vLv3uHsHCPUy06yuMUDTbTOViIuZ7KrY84oAIuhHCDMys4w5SUrCc3zdw8fXuxjP8j735wipOYsBPpF4lhmmTbxBPLVpG5z3icOsKKvE58SjJl2Q+JHristvnAtNFnhm2Eyn5onDxGKhg5UOZkVTI54kjqqaTvlCxmWV8xZnrVxlrXvyFwZz+soy12kOIYFFLEGCCAVVlFCGjRitOikWUrQf9/APNv0SuRRylcDIsYAKNMhNP/gf/O7Wyk+Mu0nBOND94jgfw0DPLtCoOc73seM0TgD/M3Clt/2VOjD9SXqtrUWPgL5t4OK6rSl7wOUOEHkyZFNuSn6aQj4PvJ/RN2WBgVsgsOb21trH6QOQpq6SN8DBITBSoOx1j3f3dvb275lWfz+lD3K7ulqJHAAAAAlwSFlzAAAuIwAALiMBeKU/dgAAAAd0SU1FB+gKGBIQBLvT9cgAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAAFUlEQVQI1wXBAQEAAACAEP9PF1CpMCnkBftjnTYAAAAAAElFTkSuQmCC',
			officeName: 'Toronto',
			status: 'BORROWED',
			borrowedFrom: '2024-11-27',
			returnDate: '2024-12-11',
		},
		{
			borrowedId: 2,
			bookCopyId: 3,
			title: 'Book Two',
			author: 'Author Two',
			coverImage:
				'iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAABhGlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9TRSktgnYo4pChOlkQFdFNq1CECqFWaNXB5NIvaNKQpLg4Cq4FBz8Wqw4uzro6uAqC4AeIs4OToouU+L+m0CLGg+N+vLv3uHsHCPUy06yuMUDTbTOViIuZ7KrY84oAIuhHCDMys4w5SUrCc3zdw8fXuxjP8j735wipOYsBPpF4lhmmTbxBPLVpG5z3icOsKKvE58SjJl2Q+JHristvnAtNFnhm2Eyn5onDxGKhg5UOZkVTI54kjqqaTvlCxmWV8xZnrVxlrXvyFwZz+soy12kOIYFFLEGCCAVVlFCGjRitOikWUrQf9/APNv0SuRRylcDIsYAKNMhNP/gf/O7Wyk+Mu0nBOND94jgfw0DPLtCoOc73seM0TgD/M3Clt/2VOjD9SXqtrUWPgL5t4OK6rSl7wOUOEHkyZFNuSn6aQj4PvJ/RN2WBgVsgsOb21trH6QOQpq6SN8DBITBSoOx1j3f3dvb275lWfz+lD3K7ulqJHAAAAAlwSFlzAAAuIwAALiMBeKU/dgAAAAd0SU1FB+gKGBIQBLvT9cgAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAAFUlEQVQI1wXBAQEAAACAEP9PF1CpMCnkBftjnTYAAAAAAElFTkSuQmCC',
			officeName: 'Kaunas',
			status: 'RETURNED',
			borrowedFrom: '2024-12-27',
			returnDate: '2024-12-15',
		},
	];

	beforeEach(() => {
		jest.clearAllMocks();
		(axios.get as jest.Mock).mockImplementation((url: string, config?: AxiosRequestConfig) => {
			if (url.endsWith('/borrowed-books')) {
				if (config?.params.status === 'RETURNED') {
					return Promise.resolve({ data: mockBorrowedBooks.filter(b => b.status === 'RETURNED') })
				} else {
					return Promise.resolve({ data: mockBorrowedBooks })
				}
			}
			return Promise.reject(new Error(`Unmocked axios request for URL: ${url}`));
		})
	});

	it('renders the component and displays borrowed books', async () => {
		render(<ReservationsView />);

		expect(screen.getByText('My Reservations')).toBeInTheDocument();
		await screen.findByText('Book One');

		expect(screen.getByText('Book One')).toBeInTheDocument();
		expect(screen.getByText('Author One')).toBeInTheDocument();
		expect(screen.getByText('Toronto')).toBeInTheDocument();
		expect(screen.getByText('Borrowed')).toBeInTheDocument();
		expect(screen.getByText('Nov 27, 2024')).toBeInTheDocument();
		expect(screen.getByText('Dec 11, 2024')).toBeInTheDocument();

		expect(screen.getByText('Book Two')).toBeInTheDocument();
		expect(screen.getByText('Author Two')).toBeInTheDocument();
		expect(screen.getByText('Kaunas')).toBeInTheDocument();
		expect(screen.getByText('Returned')).toBeInTheDocument();
		expect(screen.getByText('Dec 27, 2024')).toBeInTheDocument();
		expect(screen.getByText('Dec 15, 2024')).toBeInTheDocument();
	});

	it('filters books by status', async () => {
		render(<ReservationsView />);
		await screen.findByText('Book One');

		const filterDropdown = screen.getByText(/Filter by:/);
		fireEvent.mouseDown(filterDropdown);

		const returnedFilterOption = screen.getByRole('option', { name: 'Returned' });
		fireEvent.click(returnedFilterOption);

		await waitFor(() => {
			expect(screen.getByText('Book Two')).toBeInTheDocument();
			expect(screen.queryByText('Book One')).not.toBeInTheDocument();
		});
	});

	it('sorts books by "Oldest"', async () => {
		render(<ReservationsView />);
		await screen.findByText('Book One');

		const sortDropdown = screen.getByText(/Sort by:/);
		fireEvent.mouseDown(sortDropdown);

		const oldestSortOption = screen.getByText('Oldest');
		fireEvent.click(oldestSortOption);

		await waitFor(() =>
			expect(axios.get).toHaveBeenCalledWith(
				expect.anything(),
				expect.objectContaining({
					params: expect.objectContaining({ sortBy: 'borrowedFrom', direction: 'asc' }),
				})
			)
		);
	});

	it('displays buttons for "BORROWED" books', async () => {
		render(<ReservationsView />);
		await screen.findByText('Book One');

		const editButton = screen.getByText('Edit');
		const returnButton = screen.getByText('Return');

		expect(editButton).toBeInTheDocument();
		expect(returnButton).toBeInTheDocument();
	});

	it('does not display buttons for "RETURNED" books', async () => {
		render(<ReservationsView />);
		const bookTwoRow = await screen.findByText('Book Two');

		const bookTwoContainer = bookTwoRow.closest('tr');
		expect(bookTwoContainer).toBeInTheDocument();

		const { queryByText } = within(bookTwoContainer!);

		const editButton = queryByText('Edit');
		const checkInButton = queryByText('Check in');

		expect(editButton).not.toBeInTheDocument();
		expect(checkInButton).not.toBeInTheDocument();
	});
});
