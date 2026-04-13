import { useEffect, useState } from "react";
import "../BookDetails.css";
import OfficeDetailedForBook from "../../../models/OfficeDetailedForBook";
import { CircularProgress, Modal, Paper, Radio } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import OfficeDetails from "./OfficeDetails/OfficeDetails";
import BookDetailsResult from "../../../models/BookDetailsResult.interface";
import ReservationModalSection from "./ReservationModalSection/ReservationModalSection";
import CoverImage from "../../CoverImage/CoverImage";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import "dayjs/locale/en-ie";
import useFetch from "../../hooks/UseFetch";
import axios from "axios";
import { useParams } from "react-router";
import { readAuthTokensFromCookies } from "../../../auth/cookies";
import { useNavigate } from "react-router-dom";

interface ReservationPanelProps {
  bookDetails: BookDetailsResult;
}

export default function ReservationPanel({ bookDetails }: ReservationPanelProps) {
	const [selectedOffice, setSelectedOffice] = useState<OfficeDetailedForBook | null>(null);
	const [isReservationModalOpen, setIsReservationModalOpen] = useState(false);
	const [plannedReturnDate, setPlannedReturnDate] = useState<Dayjs | null>(null);
	const { bookId } = useParams();
	const { data: offices, fetchData } = useFetch(`/offices/book/${bookId}`);

	const navigate = useNavigate();

	useEffect(() => {
		fetchData();
	}, []);

	useEffect(() => {
		if (offices && offices.length > 0 && !selectedOffice) {
			const firstAvailableOffice = offices.findIndex((office: OfficeDetailedForBook) => office.copiesAvailable > 0);
			if (firstAvailableOffice > -1) {
				setSelectedOffice(offices[firstAvailableOffice]);
			}
		}
	}, [offices, selectedOffice]);

	function handleOfficeClick(office: OfficeDetailedForBook): void {
		if (office.copiesAvailable > 0) {
			setSelectedOffice(office);
		}
	}

	function getOfficeClassName(office: OfficeDetailedForBook): string {
		const availableOfficeCls = "hover:cursor-pointer border-gray-300";
		const unavailableOfficeCls = "hover:cursor-default border-gray-200 bg-gray-200";
		return office.copiesAvailable > 0 ? availableOfficeCls : unavailableOfficeCls;
	}

	function openReservationModal() {
		setIsReservationModalOpen(true);
	}

	function closeReservationModal() {
		setIsReservationModalOpen(false);
		setPlannedReturnDate(null);
	}

	const handleConfirmBorrow = async () => {
		const borrowData = {
			officeId: selectedOffice?.basicOffice.officeId ?? 0,
			returnDate: plannedReturnDate ? plannedReturnDate.format("YYYY-MM-DD") : "",
		};

		const bookId = bookDetails.bookId;

		const onSubmit = (bookId: number, borrowData: { officeId: number; returnDate: string }) => {
			axios
				.post(`http://localhost:8080/api/v1/books/${bookId}/borrow`, borrowData, {
					headers: {
						authorization: `Bearer ${readAuthTokensFromCookies()?.token}`,
					},
				})
				.then(() => {
					navigate("/");
				});
		};

		onSubmit(bookId, borrowData);
	};

	return offices === null ? (
		<CircularProgress />
	) : (
		<div className="flex flex-col gap-6" style={{ minWidth: "154px" }}>
			<h4 className="text-xl">
				<b>Borrow from </b>
			</h4>
			<div className="w-full flex flex-col gap-3" data-testid="offices-list">
				{offices.map((office: OfficeDetailedForBook) => (
					<div
						key={office.basicOffice.officeId}
						onClick={() => handleOfficeClick(office)}
						className={`w-full p-4 border rounded-md ${getOfficeClassName(office)}`}
					>
						<div className="flex flex-row justify-start">
							<div className="-translate-x-2 -translate-y-2.5">
								<Radio
									checked={selectedOffice?.basicOffice.officeId === office.basicOffice.officeId}
									disabled={office.copiesAvailable === 0}
									disableRipple
								/>
							</div>
							<OfficeDetails office={office} />
						</div>
					</div>
				))}
			</div>
			<button className="btn-primary p-3" disabled={selectedOffice === null} onClick={openReservationModal}>
				Borrow
			</button>
			<Modal open={isReservationModalOpen} onClose={closeReservationModal}>
				<Paper className="absolute top-2/4 left-2/4 -translate-x-1/2 -translate-y-1/2 w-1/3 p-4">
					<nav className="flex flex-row justify-end">
						<div onClick={closeReservationModal} className="cursor-pointer">
							<CloseIcon fontSize="large" />
						</div>
					</nav>
					<main className="p-6 pt-0 flex flex-col gap-6">
						<h2 className="text-2xl font-bold" data-testid="modal-header">
							Borrow
						</h2>
						<div className="grid grid-cols-8 gap-8">
							<div className="col-span-2 overflow-hidden rounded-md h-fit">
								<CoverImage coverImage={bookDetails.coverImage} />
							</div>
							<div className="col-span-6 flex flex-col gap-4">
								<header className="flex flex-col gap-1">
									<h3 className="text-xl font-bold">{bookDetails.title}</h3>
									<p className="book-details-author-label">
										<u>{bookDetails.author}</u>
									</p>
								</header>
								<ReservationModalSection headerText="Borrow from">
									<OfficeDetails office={selectedOffice!} compact />
								</ReservationModalSection>
								<ReservationModalSection headerText="Planned return date">
									<LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en-ie">
										<DatePicker
											value={plannedReturnDate}
											onChange={(date) => setPlannedReturnDate(date)}
											disablePast
											maxDate={dayjs().add(1, "month")}
											className="w-full font-bold"
										/>
									</LocalizationProvider>
								</ReservationModalSection>
							</div>
						</div>
					</main>
					<footer className="mt-20 flex flex-row justify-end gap-2">
						<button className="btn-secondary px-5" onClick={closeReservationModal}>
							Cancel
						</button>
						<button className="btn-primary px-5 py-1.5" disabled={plannedReturnDate === null} onClick={handleConfirmBorrow}>
							Confirm borrow
						</button>
					</footer>
				</Paper>
			</Modal>
		</div>
	);
}
