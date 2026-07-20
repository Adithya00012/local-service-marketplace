import { useState } from 'react';
import { askChatbot } from '../api/ai';

function ChatWidget() {
    const [open, setOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);

    async function handleSend(e) {
        e.preventDefault();
        if (!input.trim()) return;

        const question = input;
        setMessages([...messages, { role: 'user', text: question }]);
        setInput('');
        setLoading(true);

        try {
            const result = await askChatbot(question);
            setMessages((prev) => [...prev, { role: 'bot', text: result.answer }]);
        } catch (err) {
            setMessages((prev) => [...prev, { role: 'bot', text: 'Sorry, something went wrong.' }]);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="fixed bottom-6 right-6 z-50">
            {open ? (
                <div className="bg-white w-80 h-96 rounded-lg shadow-xl flex flex-col border border-gray-200">
                    <div className="bg-purple-600 text-white p-3 rounded-t-lg flex justify-between items-center">
                        <span className="font-semibold text-sm">✨ Ask our AI Assistant</span>
                        <button onClick={() => setOpen(false)} className="text-white">✕</button>
                    </div>

                    <div className="flex-1 overflow-y-auto p-3 space-y-2">
                        {messages.length === 0 && (
                            <p className="text-xs text-gray-400">
                                Ask me things like "I need help fixing my sink" or "any yoga classes available?"
                            </p>
                        )}
                        {messages.map((m, i) => (
                            <div
                                key={i}
                                className={`text-xs p-2 rounded-lg max-w-[85%] ${m.role === 'user'
                                        ? 'bg-purple-100 ml-auto text-right'
                                        : 'bg-gray-100'
                                    }`}
                            >
                                {m.text}
                            </div>
                        ))}
                        {loading && <p className="text-xs text-gray-400">Thinking...</p>}
                    </div>

                    <form onSubmit={handleSend} className="p-2 border-t border-gray-200 flex gap-2">
                        <input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            placeholder="Type a question..."
                            className="flex-1 text-xs px-2 py-1 border border-gray-300 rounded"
                        />
                        <button
                            type="submit"
                            className="bg-purple-600 text-white text-xs px-3 py-1 rounded"
                        >
                            Send
                        </button>
                    </form>
                </div>
            ) : (
                <button
                    onClick={() => setOpen(true)}
                    className="bg-purple-600 text-white w-14 h-14 rounded-full shadow-lg text-xl hover:bg-purple-700"
                >
                    💬
                </button>
            )}
        </div>
    );
}

export default ChatWidget;