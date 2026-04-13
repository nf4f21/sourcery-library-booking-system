import { useForm, SubmitHandler, useFieldArray } from "react-hook-form";
import TextField from "@mui/material/TextField";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import "./BookForm.css";
import { useState, useRef, useEffect } from "react";
import dayjs from "dayjs";
import OfficeBasicDetails from "../../models/OfficeBasicDetails";
import axios from "axios";

export const bookSchema = z.object({
  coverImage: z.instanceof(Blob, { message: "Image is required" }),
  title: z.string().min(1, { message: "Title is required" }),
  author: z.string().min(1, { message: "Author is required" }),
  description: z.string().min(1, { message: "Description is required" }),
  isbn: z.string().min(1, { message: "Isbn is required" }),
  format: z.string().min(1, { message: "Format is required" }),
  numberOfPages: z
    .number({
      required_error: "Number of pages is required",
      invalid_type_error: "Number of pages must be a number",
    })
    .int({ message: "Number of pages must be an integer" })
    .positive({ message: "Number of pages must be a positive number" }),
  publicationDate: z.string({ message: "Publication date is required" }).date(),
  publisher: z.string().min(1, { message: "Publisher is required" }),
  editionLanguage: z
    .string()
    .min(1, { message: "Edition language is required" }),
  series: z.string().min(1, { message: "Series is required" }),
  category: z.string().min(1, { message: "Category is required" }),
  newBookCopies: z.array(
    z.object({
      officeId: z.number(),
      copyCount: z
        .number({
          required_error: "Number of copies is required",
          invalid_type_error: "Number of copies must be a number",
        })
        .min(0, { message: "Number of copies cannot be negative" })
        .int({ message: "Number of copies must be an integer" }),
    })
  ),
});

export type BookFormFields = z.infer<typeof bookSchema>;

interface BookFromProps {
  initialData?: Partial<BookFormFields>;
  onSubmit: (data: BookFormFields) => void;
}

export const SelectOptions = {
  format: ["Hardcover", "Trade Paperback", "Mass Market Paperback"],
  publisher: ["Vintage Publishing", "HarperCollins", "MacMillan Publishers"],
  editionLanguage: ["English", "Polish", "French"],
  category: [
    "Development",
    "Business",
    "Computer science",
    "Data science",
    "Design",
    "Productivity",
  ],
};

const BookForm = ({ initialData, onSubmit }: BookFromProps) => {
  const {
    register: book,
    handleSubmit,
    setValue,
    getValues,
    trigger,
    control,
    formState: { errors },
    reset,
  } = useForm<BookFormFields>({
    resolver: zodResolver(bookSchema),
    mode: "onBlur",
    defaultValues: initialData,
  });

	const { fields: offices } = useFieldArray({
		control,
		name: 'newBookCopies',
	});

  const [image, setImage] = useState<string | null>(null);
  const [officesNames, setOfficesNames] = useState<Record<string, string>>({});
  const imageInputRef = useRef<HTMLInputElement>(null);

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setValue("coverImage", file);
      const imageUrl = URL.createObjectURL(file);
      setImage(imageUrl);
      trigger("coverImage");
    }
  };

  const fetchOfficeData = () => {
    axios
      .get("http://localhost:8080/api/v1/offices")
      .then((response) => {
        const offices = response.data;
        setOfficesNames(
          offices.reduce(
            (acc: Record<string, string>, office: OfficeBasicDetails) => {
              acc[office.officeId] = office.name;
              return acc;
            },
            {}
          )
        );
        if (!initialData?.newBookCopies) {
          setValue(
            "newBookCopies",
            offices.map((office: OfficeBasicDetails) => ({
              officeId: office.officeId,
              copyCount: 0,
            }))
          );
        }
      })
      .catch((error) => {
        console.error(
          "Server Error caught while fetching office data:",
          error.response.data
        );
      });
  };

  useEffect(() => {
    fetchOfficeData();

    if (initialData?.coverImage instanceof Blob) {
      setImage(URL.createObjectURL(initialData.coverImage));
    }
  }, [initialData]);

  const handleFormSubmit: SubmitHandler<BookFormFields> = (data) => {
    onSubmit(data);
  };

  return (
    <div className="book-form-main-register">
      <form
        className="book-form-main"
        onSubmit={handleSubmit(handleFormSubmit)}
      >
        {initialData ? (
          <h1 className="book-form-h1">Edit Book</h1>
        ) : (
          <h1 className="book-form-h1">Register New Book</h1>
        )}
        <label className="book-form-label">COVER</label>
        <div {...book("coverImage")} className="book-form-image-cover">
          {image ? (
            <img
              src={image}
              alt="Uploaded Cover"
              style={{ width: "100%", height: "100%", objectFit: "cover" }}
            />
          ) : (
            <>
              <CloudUploadIcon
                className="book-form-cloud-icon"
                fontSize="inherit"
              />
              <span
                className="book-form-text-upload"
                onClick={() => imageInputRef.current?.click()}
              >
                Upload image
              </span>
            </>
          )}
        </div>
        <label
          className="book-form-custom-file-upload"
          style={!getValues().coverImage ? { display: "none" } : {}}
        >
          Browse images...
          <input
            type="file" 
            className="book-form-file-input"
            id="imageInput"
            accept="image/*"
            ref={imageInputRef}
            onChange={handleImageUpload}
          />
        </label>
        {errors.coverImage && (
          <div className="book-form-errors">{errors.coverImage.message}</div>
        )}
        <label className="book-form-label">TITLE</label>
        <TextField
          className="book-form-input"
          {...book("title")}
          placeholder="Enter title"
        />
        {errors.title && (
          <div className="book-form-errors">{errors.title.message}</div>
        )}
        <label className="book-form-label">AUTHOR (S)</label>
        <TextField
          className="book-form-input"
          {...book("author")}
          placeholder="Enter the author(s)"
        />
        {errors.author && (
          <div className="book-form-errors">{errors.author.message}</div>
        )}
        <label className="book-form-label">DESCRIPTION</label>
        <TextField
          className="book-form-input"
          {...book("description")}
          placeholder="Enter the description"
          multiline
          rows={15}
          variant="outlined"
        />
        {errors.description && (
          <div className="book-form-errors">{errors.description.message}</div>
        )}
        <label className="book-form-label">ISBN</label>
        <TextField
          className="book-form-input"
          {...book("isbn")}
          placeholder="Enter the isbn"
        />
        {errors.isbn && (
          <div className="book-form-errors">{errors.isbn.message}</div>
        )}
        <label className="book-form-label">FORMAT</label>
        <FormControl fullWidth>
          <Select
            {...book("format")}
            name="format"
            sx={{ "& .MuiSelect-icon": { color: "black" } }}
            defaultValue={initialData?.format || ""}
            displayEmpty
            renderValue={(selected) =>
              !selected ? (
                <span style={{ color: "#aaa" }}>Select a format</span>
              ) : (
                selected
              )
            }
          >
            {SelectOptions.format.map((format) => (
              <MenuItem key={format} value={format}>
                {format}
              </MenuItem>
            ))}
          </Select>
          {errors.format && (
            <div className="book-form-errors">{errors.format.message}</div>
          )}
        </FormControl>
        <label className="book-form-label">NUMBER OF PAGES</label>
        <TextField
          className="book-form-input"
          {...book("numberOfPages", {
            setValueAs: (v) => (v === "" ? undefined : parseInt(v, 10)),
          })}
          placeholder="Enter the number of pages"
        />
        {errors.numberOfPages && (
          <div className="book-form-errors">{errors.numberOfPages.message}</div>
        )}
        <label className="book-form-label">PUBLICATION DATE</label>
        <div className="book-form-input">
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DemoContainer
              components={["DatePicker"]}
              sx={{ paddingTop: "0px" }}
            >
              <DatePicker
                sx={{ width: "100%", svg: { color: "black" } }}
                format="DD MMMM YYYY"
                value={
                  getValues("publicationDate")
                    ? dayjs(getValues("publicationDate"))
                    : null
                }
                disableFuture
                onChange={(newValue) => {
                  // Ensure newValue is a Dayjs object before converting to string
                  const dateString = newValue
                    ? newValue.format("YYYY-MM-DD")
                    : "";
                  setValue("publicationDate", dateString);
                  // trigger validation after converting to string
                  trigger("publicationDate");
                }}
              />
            </DemoContainer>
          </LocalizationProvider>
        </div>
        {errors.publicationDate && (
          <div className="book-form-errors">
            {errors.publicationDate.message}
          </div>
        )}
        <label className="book-form-label">PUBLISHER</label>
        <FormControl fullWidth>
          <Select
            {...book("publisher")}
            name="publisher"
            sx={{ "& .MuiSelect-icon": { color: "black" } }}
            defaultValue={initialData?.publisher || ""}
            displayEmpty
            renderValue={(selected) =>
              !selected ? (
                <span style={{ color: "#aaa" }}>Select a publisher</span>
              ) : (
                selected
              )
            }
          >
            {SelectOptions.publisher.map((publisher) => (
              <MenuItem key={publisher} value={publisher}>
                {publisher}
              </MenuItem>
            ))}
          </Select>
          {errors.publisher && (
            <div className="book-form-errors">{errors.publisher?.message}</div>
          )}
        </FormControl>
        <label className="book-form-label">EDITION LANGUAGE</label>
        <FormControl fullWidth>
          <Select
            {...book("editionLanguage")}
            name="editionLanguage"
            sx={{ "& .MuiSelect-icon": { color: "black" } }}
            defaultValue={initialData?.editionLanguage || ""}
            displayEmpty
            renderValue={(selected) =>
              !selected ? (
                <span style={{ color: "#aaa" }}>
                  Select an edition language
                </span>
              ) : (
                selected
              )
            }
          >
            {SelectOptions.editionLanguage.map((editionLanguage) => (
              <MenuItem key={editionLanguage} value={editionLanguage}>
                {editionLanguage}
              </MenuItem>
            ))}
          </Select>
          {errors.editionLanguage && (
            <div className="book-form-errors">
              {errors.editionLanguage.message}
            </div>
          )}
        </FormControl>
        <label className="book-form-label">SERIES</label>
        <TextField
          className="book-form-input"
          {...book("series")}
          placeholder="Enter the series"
        />
        {errors.series && (
          <div className="book-form-errors">{errors.series.message}</div>
        )}
        <label className="book-form-label">CATEGORY</label>
        <FormControl fullWidth>
          <Select
            {...book("category")}
            name="category"
            sx={{ "& .MuiSelect-icon": { color: "black" } }}
            defaultValue={initialData?.category || ""}
            displayEmpty
            renderValue={(selected) =>
              !selected ? (
                <span style={{ color: "#aaa" }}>Select a category</span>
              ) : (
                selected
              )
            }
          >
            {SelectOptions.category.map((category) => (
              <MenuItem key={category} value={category}>
                {category}
              </MenuItem>
            ))}
          </Select>
          {errors.category && (
            <div className="book-form-errors">{errors.category.message}</div>
          )}
        </FormControl>
        <label className="book-form-label">COPIES AVAILABLE AT OFFICES</label>
        {offices.map((office, index) => (
          <div
            key={office.officeId}
            id={`${office.officeId}`}
            className="book-form-office-field"
          >
            <div className="book-form-office-name-field">
              <TextField
                sx={{
                  "& .MuiInputBase-input.Mui-disabled": {
                    WebkitTextFillColor: "#000000",
                  },
                  width: "100%",
                }}
                defaultValue={officesNames[office.officeId]}
                disabled
              />
            </div>
            <div className="book-form-office-count-field">
              <TextField
                {...book(`newBookCopies.${index}.copyCount`, {
                  setValueAs: (v) => (v === "" ? undefined : parseInt(v, 10)),
                })}
                sx={{ width: "100%" }}
                placeholder="number of copies"
                defaultValue={initialData?.newBookCopies?.[index].copyCount}
              />
              {errors.newBookCopies && (
                <div className="book-form-errors">
                  {errors.newBookCopies[index]?.copyCount?.message}
                </div>
              )}
            </div>
          </div>
        ))}
        <button type="submit" className="book-form-register-button">
          {initialData ? "Edit" : "Register"}
        </button>
      </form>
    </div>
  );
};

export default BookForm;
