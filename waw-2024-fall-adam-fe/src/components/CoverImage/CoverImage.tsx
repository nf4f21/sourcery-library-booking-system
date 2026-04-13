import React, { useEffect, useState } from "react";
import { Buffer } from "buffer";
import CircularProgress from "@mui/material/CircularProgress";

interface ImageProps {
  coverImage: string;
}

const CoverImage: React.FC<ImageProps> = ({ coverImage }) => {
  const [imageSrc, setImageSrc] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (coverImage && coverImage.length !== 0) {
      const imageData = Buffer.from(coverImage, "base64");
      const blob = new Blob([imageData]);
      const imgURL = URL.createObjectURL(blob);
      setImageSrc(imgURL);
      setLoading(false);

      return () => {
        URL.revokeObjectURL(imgURL);
      };
    } else {
      setLoading(false);
    }
  }, [coverImage]);

  if (loading) {
    return (
      <div className="cover-image-loading">
        <CircularProgress />
      </div>
    );
  }

  return imageSrc ? (
    <img src={imageSrc} alt="Cover" />
  ) : (
    <div>No image available</div>
  );
};

export default CoverImage;