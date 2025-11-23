# broker.py
import asyncio
from typing import Dict, List, Any


class EventBroker:
    def __init__(self):
        # correlation_id → list of listeners (SSE consumers)
        self.connections: Dict[str, List[asyncio.Queue]] = {}

    async def subscribe(self, correlation_id: str) -> asyncio.Queue:
        queue = asyncio.Queue()

        if correlation_id not in self.connections:
            self.connections[correlation_id] = []

        self.connections[correlation_id].append(queue)
        return queue

    async def publish(self, correlation_id: str, message: Any):
        if correlation_id not in self.connections:
            # No listeners yet — ignore
            return

        for queue in self.connections[correlation_id]:
            await queue.put(message)

    async def unsubscribe(self, correlation_id: str, queue: asyncio.Queue):
        if correlation_id in self.connections:
            self.connections[correlation_id].remove(queue)
            if not self.connections[correlation_id]:
                del self.connections[correlation_id]


broker = EventBroker()
