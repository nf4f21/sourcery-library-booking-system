import { PropsWithChildren } from "react";

interface ReservationModalSectionProps {
  headerText: string;
}

export default function ReservationModalSection({ children, headerText }: PropsWithChildren<ReservationModalSectionProps>) {
  return (
    <section className='border-t border-gray-400 pt-6 mt-6'>
      <header className='mb-6 uppercase text-gray-400 text-sm font-semibold'>{headerText}</header>
      <main>{children}</main>
    </section>
  )
}
