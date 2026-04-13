enum BorrowedStatus {
	BORROWED = 'BORROWED',
	RETURNED = 'RETURNED',
	REQUESTED = 'REQUESTED',
}

interface BorrowedBooks {
	borrowedId: number;
	userId: number;
	bookCopyId: number;
	title: string;
	author: string;
	coverImage: string;
	officeName: string;
	status: BorrowedStatus;
	borrowedFrom: Date;
	returnDate: Date;
}

export default BorrowedBooks;
