import OfficeDetailedForBook from "../../../../models/OfficeDetailedForBook";
import BusinessIcon from "@mui/icons-material/Business";

interface OfficeDetailsProps {
  office: OfficeDetailedForBook;
  compact?: boolean;
}

export default function OfficeDetails({
  office,
  compact = false,
}: OfficeDetailsProps) {
  return (
    <div
      className={`flex-grow flex gap-2 ${compact ? "flex-row" : "flex-col"}`}
    >
      <BusinessIcon />
      <section className={`flex flex-col ${compact ? "gap-1" : "gap-2"}`}>
        <header
          className={`font-bold pt-0.5 flex ${
            compact ? "flex-row items-center gap-2" : "flex-col"
          }`}
        >
          <p>{office.basicOffice.name + " office" + (compact ? "," : "")} </p>
          {office.copiesAvailable > 0 ? (
            <p className="text-green-500 leading-none">
              {office.copiesAvailable} available
            </p>
          ) : (
            <p className="text-red-500 leading-none">Currently unavailable</p>
          )}
        </header>
        <p className="text-gray-500">{office.address}</p>
      </section>
    </div>
  );
}
