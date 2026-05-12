import cors from "cors";
import dotenv from "dotenv";
import express, { Request, Response } from "express";

dotenv.config();

type Tour = {
  id: string;
  name: string;
  location: string;
  description: string;
  price: number;
  duration: string;
  availableSeats: number;
};

const tours: Tour[] = [
  {
    id: "t1",
    name: "Da Nang - Hoi An",
    location: "Da Nang",
    description: "Tour bien My Khe, Ba Na Hills va pho co Hoi An.",
    price: 3500000,
    duration: "3 ngay 2 dem",
    availableSeats: 20
  },
  {
    id: "t2",
    name: "Da Lat nghi duong",
    location: "Lam Dong",
    description: "Tham quan Lang Biang, thung lung tinh yeu va cho dem Da Lat.",
    price: 2900000,
    duration: "3 ngay 2 dem",
    availableSeats: 15
  },
  {
    id: "t3",
    name: "Phu Quoc bien dao",
    location: "Kien Giang",
    description: "Trai nghiem bien xanh, cap treo Hon Thom va am thuc hai san.",
    price: 5200000,
    duration: "4 ngay 3 dem",
    availableSeats: 12
  }
];

const app = express();
const port = Number(process.env.PORT ?? 8082);
const host = process.env.HOST ?? "0.0.0.0";

app.use(cors());
app.use(express.json());

app.get("/health", (_req: Request, res: Response) => {
  res.json({ service: "tour-service", status: "UP" });
});

app.get("/tours", (_req: Request, res: Response) => {
  res.json(tours);
});

app.get("/tours/:id", (req: Request, res: Response) => {
  const tour = tours.find((item) => item.id === req.params.id);

  if (!tour) {
    return res.status(404).json({ message: "Khong tim thay tour" });
  }

  return res.json(tour);
});

app.listen(port, host, () => {
  console.log(`Tour Service running at http://${host}:${port}`);
});

