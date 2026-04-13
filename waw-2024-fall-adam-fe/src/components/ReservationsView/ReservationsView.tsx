import axios from 'axios';
import './ReservationsView.css';
import { useEffect, useState } from 'react';
import {
	Table,
	TableBody,
	TableCell,
	TableContainer,
	TableHead,
	TableRow,
	Paper,
	Select,
	MenuItem,
	FormControl,
} from '@mui/material';
import BorrowedBooks from '../../models/ReservationsViewComponent/BorrowedBooks';
import CoverImage from '../CoverImage/CoverImage';
import BorrowedBooksSortOptions from '../../models/ReservationsViewComponent/BorrowedBooksSortOptions';
import BorrowedBooksFilterOptions from '../../models/ReservationsViewComponent/BorrowedBooksFilterOptions';
import Button from '@mui/material/Button';
import { readAuthTokensFromCookies } from '../../auth/cookies';
import { SelectChangeEvent } from '@mui/material/Select/SelectInput';

const ReservationsView = () => {
	const [borrowedBooks, setBorrowedBooks] = useState<BorrowedBooks[]>([]);
	const [sortOption, setSortOption] = useState<BorrowedBooksSortOptions>(BorrowedBooksSortOptions.Recent);
	const [filterOption, setFilterOption] = useState<BorrowedBooksFilterOptions>(BorrowedBooksFilterOptions.All);

	const fetchBorrowedBooksData = () => {
		const sortMappings = {
			Recent: { sortBy: 'borrowedFrom', direction: 'desc' },
			Oldest: { sortBy: 'borrowedFrom', direction: 'asc' },
		};

		const filterMappings: Record<BorrowedBooksFilterOptions, string> = {
			[BorrowedBooksFilterOptions.All]: '',
			[BorrowedBooksFilterOptions.Borrowed]: 'BORROWED',
			[BorrowedBooksFilterOptions.Returned]: 'RETURNED',
		};

		const filterBy = filterMappings[filterOption];

		const { sortBy, direction } = sortMappings[sortOption];

		axios
			.get('http://localhost:8080/api/v1/borrowed-books', {
				headers: {
					Authorization: `Bearer ${readAuthTokensFromCookies()?.token}`,
				},
				params: {
					sortBy,
					direction,
					pageNumber: 0,
					pageSize: 10,
					status: filterBy,
				},
			})
			.then((response) => {
				setBorrowedBooks(response.data);
			})
			.catch((error) => {
				console.error('Error fetching borrowed books:', error);
			});
	};

	const handleSortChange = (event: SelectChangeEvent<BorrowedBooksSortOptions>) => {
		setSortOption(event.target.value as BorrowedBooksSortOptions);
	};

	const handleFilterChange = (event: SelectChangeEvent<BorrowedBooksFilterOptions>) => {
		setFilterOption(event.target.value as BorrowedBooksFilterOptions);
	};

	useEffect(() => {
		fetchBorrowedBooksData();
	}, [sortOption, filterOption]);

	return (
		<div className='reservations-view'>
			<h1>My Reservations</h1>

			<div className='reservations-view-filter-and-sort'>
				<FormControl className='reservations-view-filter' variant='outlined' size='small'>
					<Select
						value={filterOption}
						onChange={handleFilterChange}
						displayEmpty
						sx={{
							fontSize: 14,
							color: '#1A1A2E',
							borderColor: '#E0E0E0',
						}}
						renderValue={(selected) => (
							<span>
								Filter by: <strong style={{ paddingLeft: '10px' }}>{selected}</strong>
							</span>
						)}
					>
						<MenuItem value={BorrowedBooksFilterOptions.All}>All</MenuItem>
						<MenuItem value={BorrowedBooksFilterOptions.Borrowed}>Borrowed</MenuItem>
						<MenuItem value={BorrowedBooksFilterOptions.Returned}>Returned</MenuItem>
						{/* // 'Requested' status is to be added later */}
					</Select>
				</FormControl>
				<FormControl className='reservations-view-sort' variant='outlined' size='small'>
					<Select
						value={sortOption}
						onChange={handleSortChange}
						displayEmpty
						sx={{
							fontSize: 14,
							color: '#1A1A2E',
							borderColor: '#E0E0E0',
						}}
						renderValue={(selected) => (
							<span>
								Sort by: <strong style={{ paddingLeft: '10px' }}>{selected}</strong>
							</span>
						)}
					>
						<MenuItem value={BorrowedBooksSortOptions.Recent}>Recent</MenuItem>
						<MenuItem value={BorrowedBooksSortOptions.Oldest}>Oldest</MenuItem>
					</Select>
				</FormControl>
			</div>
			<TableContainer className='reservations-view-main-table' component={Paper}>
				<Table>
					<TableHead>
						<TableRow>
							<TableCell className='reservations-view-book-label'>BOOK</TableCell>
							<TableCell className='reservations-view-book-label'>OFFICE</TableCell>
							<TableCell className='reservations-view-book-label'>STATUS</TableCell>
							<TableCell className='reservations-view-book-label'>BOOKED FROM</TableCell>
							<TableCell className='reservations-view-book-label'>RETURN DATE</TableCell>
							<TableCell className='reservations-view-book-label'></TableCell>
						</TableRow>
					</TableHead>
					<TableBody>
						{borrowedBooks.map((borrowedBook) => (
							<TableRow className='reservations-view-row' key={borrowedBook.borrowedId}>
								<TableCell className='reservations-view-table-cell'>
									<div className='reservations-view-book-info'>
										<div className='reservations-view-book-image'>
											<CoverImage coverImage={borrowedBook.coverImage} />
										</div>
										<div className='reservations-view-book-details'>
											<div className='reservations-view-book-title'>{borrowedBook.title}</div>
											<div className='reservations-view-book-author'>{borrowedBook.author}</div>
										</div>
									</div>
								</TableCell>
								<TableCell className='reservations-view-table-cell'>
									<div className='reservations-view-book-info-right-element'>{borrowedBook.officeName}</div>
								</TableCell>
								<TableCell className='reservations-view-table-cell'>
									<div className='reservations-view-book-info-right-status'>
										{borrowedBook.status === 'BORROWED' ? (
											<div className='reservations-view-book-status-borrowed'>
												{borrowedBook.status.charAt(0) + borrowedBook.status.substring(1).toLowerCase()}
											</div>
										) : (
											<div className='reservations-view-book-status-returned'>
												{borrowedBook.status.charAt(0) + borrowedBook.status.substring(1).toLowerCase()}
											</div>
										)}
									</div>
								</TableCell>
								<TableCell className='reservations-view-table-cell'>
									<div className='reservations-view-book-info-right-element'>
										{new Date(borrowedBook.borrowedFrom).toLocaleDateString('en-US', {
											month: 'short',
											day: 'numeric',
											year: 'numeric',
										})}
									</div>
								</TableCell>
								<TableCell className='reservations-view-table-cell'>
									<div className='reservations-view-book-info-right-element'>
										{new Date(borrowedBook.returnDate).toLocaleDateString('en-US', {
											month: 'short',
											day: 'numeric',
											year: 'numeric',
										})}
									</div>
								</TableCell>
								<TableCell className='reservations-view-table-cell'>
									{borrowedBook.status === 'BORROWED' && (
										<div className='reservations-view-buttons-container'>
											<Button className='reservations-view-edit-button'>Edit</Button>
											<Button className='reservations-view-return-button'>Return</Button>
										</div>
									)}
								</TableCell>
							</TableRow>
						))}
					</TableBody>
				</Table>
			</TableContainer>
		</div>
	);
};

export default ReservationsView;
