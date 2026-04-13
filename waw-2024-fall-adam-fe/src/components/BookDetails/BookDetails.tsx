import { useState, useEffect } from "react";
import { useParams } from "react-router";
import "./BookDetails.css";
import "../../App.css";
import BookDetailsResult from "../../models/BookDetailsResult.interface";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import ReservationPanel from "./ReservationPanel/ReservationPanel";
import CoverImage from "../CoverImage/CoverImage";
import useFetch from "../hooks/UseFetch";
import CircularProgress from "@mui/material/CircularProgress";
import EditIcon from "@mui/icons-material/Edit";
import { useNavigate } from "react-router";
import { useSelector } from "react-redux";
import { RootState } from "../../store/store";
import ActiveReservation from "../../models/ActiveReservation.interface";
import { Button, Paper} from "@mui/material";

const BookDetails = () => {
  const { bookId } = useParams();
  const isAdmin = useSelector((state: RootState) => state.user.isAdmin);
  const navigate = useNavigate();

  const {
		data: bookDetailsData,
		loading: bookLoading,
		error: bookError,
		fetchData: fetchBookDetails,
  } = useFetch<BookDetailsResult>(`/books/${bookId}`);

  const {
		data: availabilityData,
		loading: availabilityLoading,
		error: availabilityError,
		fetchData: fetchBookAvailability,
  } = useFetch<boolean>(`/books/${bookId}/availability`);

  const {
		data: reservationsData,
		loading: reservationLoading,
		error: reservationError,
		fetchData: fetchActiveReservations,
  } = useFetch<ActiveReservation[]>(`/books/${bookId}/active-reservations`);

  const handleEditClick = () => {
		navigate(`/books/${bookId}/edit`);
  };

  useEffect(() => {
    // Fetch office data when the component mounts - no dependencies.
    fetchBookDetails();
    fetchBookAvailability();
    fetchActiveReservations();
  }, []);

  const toStringDate = (e: Date | undefined) => {
    return e ? new Date(e.toString()).toDateString().substring(4) : null;
  };

  return (
		<>
			{bookLoading && availabilityLoading && reservationLoading && (
				<div className="loading-container">
					<CircularProgress />
				</div>
			)}
			{bookError && <div>Error: {bookError}</div>}
			{availabilityError && <div>Error: {availabilityError}</div>}
			{reservationError && <div>Error: {reservationError}</div>}
			{bookDetailsData && (
				<div className="book-details-container">
					<div className="book-details-image">
						<CoverImage coverImage={bookDetailsData.coverImage} />
					</div>
					<div className="book-details-info">
						<div className="book-details-info-title">{bookDetailsData?.title}</div>
						<p className="book-details-info-author">
							By <u>{bookDetailsData?.author}</u>
						</p>
						<div className="book-details-info-status-container">
							<div className="book-details-info-status">
								<CheckCircleIcon className={`${availabilityData ? "text-green-600" : "text-red-600"}`} sx={{ fontSize: "16px" }} />
                <span className={`${availabilityData ? "text-green-600" : "text-red-600"} ml-2`}>
                  {availabilityData ? "Available" : "Currently unavailable"}
                </span>
							</div>
						</div>
						<Paper className="book-details-info-description-container">
							<div className="book-details-info-description">{bookDetailsData?.description}</div>
						</Paper>
						<Paper className="book-details-info-details-container">
							<div className="book-details-info-details-title">Details</div>
							<div className="book-details-info-details">
								<div className="book-details-info-details-element">
									<p className="book-details-info-details-text"> Format </p>
									<p className="book-details-info-details-book-data">
										{" "}
										{bookDetailsData?.format} | {bookDetailsData?.numberOfPages} pages{" "}
									</p>
								</div>
								<div className="book-details-info-details-element">
									<p className="book-details-info-details-text"> Publication Date </p>
									<p className="book-details-info-details-book-data">{toStringDate(bookDetailsData?.publicationDate)}</p>
								</div>
								<div className="book-details-info-details-element">
									<p className="book-details-info-details-text"> Publisher </p>
									<p className="book-details-info-details-book-data">{bookDetailsData?.publisher}</p>
								</div>
								<div className="book-details-info-details-element">
									<p className="book-details-info-details-text"> ISBN </p>
									<p className="book-details-info-details-book-data"> {bookDetailsData?.isbn}</p>
								</div>
								<div className="book-details-info-details-element">
									<p className="book-details-info-details-text"> Edition Language </p>
									<p className="book-details-info-details-book-data">{bookDetailsData?.editionLanguage} </p>
								</div>
								<div className="book-details-info-details-element">
									<p className="book-details-info-details-text"> Series </p>
									<p className="book-details-info-details-book-data"> {bookDetailsData?.series}</p>
								</div>
								<div className="book-details-info-details-element">
									<p className="book-details-info-details-text"> Category </p>
									<p className="book-details-info-details-book-data"> {bookDetailsData?.category}</p>
								</div>
							</div>
						</Paper>
						{isAdmin && (
							<div className="book-details-button-container">
								<Button onClick={handleEditClick} className="book-details-button-edit">
									<span className="book-details-button-edit-elements">
										<EditIcon className="book-details-button-edit-icon" fontSize="inherit" />
									</span>
									<span>Edit details</span>
								</Button>
							</div>
						)}
            {reservationsData && reservationsData.length > 0 && (
              <TableContainer className="active-reservations-view-table" component={Paper}>
                <div className="active-reservations-view-table-title">Active Reservations</div>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow className="active-reservations-view-table-header">
                      <TableCell className="active-reservations-view-table-label-employee">EMPLOYEE</TableCell>
                      <TableCell className="active-reservations-view-table-label-office">OFFICE</TableCell>
                      <TableCell className="active-reservations-view-table-label-booked-from">BOOKED FROM</TableCell>
                      <TableCell className="active-reservations-view-table-label-return-date">RETURN DATE</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {reservationsData.map((row) => (
                      <TableRow className="active-reservations-view-row" key={row.firstName}>
                        <TableCell className="active-reservations-view-row-employee" component="td" scope="row">
                          <b> {row.firstName + "  " + row.lastName}</b>
                        </TableCell>
                        <TableCell className="active-reservations-view-row-office" component="td" scope="row">
                          {row.officeName}
                        </TableCell>
                        <TableCell className="active-reservations-view-row-borroed-from" component="td">
                          {toStringDate(row.borrowedFrom)}
                        </TableCell>
                        <TableCell className="active-reservations-view-row-return-date" component="td">
                          {toStringDate(row.returnDate)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
					</div>
					<div className="book-details-reservation-panel">
						<ReservationPanel bookDetails={bookDetailsData!} />
					</div>
				</div>
			)}
		</>
  );
};

export default BookDetails;
