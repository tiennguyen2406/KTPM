const express = require("express");
const amqp = require("amqplib");

const app = express();
app.use(express.json());

const RABBITMQ_URL = "amqp://user:password@rabbitmq:5672";
const QUEUE = "order_queue";
const DEAD_LETTER_QUEUE = "order_queue.dlq";

let channel;

async function connectRabbitMQ() {
  while (true) {
    try {
      const conn = await amqp.connect(RABBITMQ_URL);
      channel = await conn.createChannel();
      await channel.assertQueue(QUEUE, {
        durable: true,
        deadLetterExchange: "",
        deadLetterRoutingKey: DEAD_LETTER_QUEUE,
      });

      console.log("Producer connected to RabbitMQ");
      break;
    } catch {
      console.log("Waiting for RabbitMQ...");
      await new Promise((r) => setTimeout(r, 3000));
    }
  }
}

app.post("/send", async (req, res) => {
  const { message, orderId } = req.body;

  // Gói dữ liệu mà không cần kiểm tra kỹ ở đây để test lỗi ở Consumer
  const data = {
    message: message,
    orderId: orderId, // Chấp nhận cả "BB" hoặc bất cứ thứ gì
    timestamp: new Date()
  };

  channel.sendToQueue(
    QUEUE,
    Buffer.from(JSON.stringify(data)),
    { persistent: true }
  );

  console.log("Sent to queue (waiting for processing):", data);
  res.json({ status: "sent_to_queue", dataSent: data });
});

connectRabbitMQ();

app.listen(3000, () => {
  console.log("Producer API listening on port 3000");
});
