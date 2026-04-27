// server.js
const WebSocket = require("ws");
const wss = new WebSocket.Server({ port: 8080 });

console.log("✅ Secure WebSocket server running on ws://localhost:8080/ws/chat");

// Handle new client connection
wss.on("connection", (ws, req) => {
    console.log("🔗 New client connected");

    ws.on("message", (message) => {
        console.log("📩 Received:", message.toString());
        // Broadcast message to all connected clients
        wss.clients.forEach(client => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(message.toString());
            }
        });
    });

    ws.on("close", () => console.log("❌ Client disconnected"));
});
