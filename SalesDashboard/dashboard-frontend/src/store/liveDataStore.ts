import type {AggRow} from "./schemas.ts";
import {create} from "zustand/react";
import {STREAM_URL} from "./client.ts";

interface LiveDataStore {

    liveData: AggRow[] | null;
    error?: string;
    listening: boolean;

    listenStream: () => Promise<void>;
    clearData: () => void;
}


export const useLiveDataStore = create<LiveDataStore>((set, get) => ({
    liveData: [],
    listening: false,

    listenStream: async () => {
        if (get().listening) return;
        set({listening: true})

        try {
            const res = await fetch(STREAM_URL)
            if (!res.body){
                set({error: "Empty response from a stream"});
                return;
            }

            const reader = res.body.getReader();
            const decoder = new TextDecoder("utf-8");
            let buffer = ""

            while (true) {
                const {done, value} = await reader.read();
                if (done) break;

                buffer +=decoder.decode(value , {stream: true});
                const lines = buffer.split("\n");
                buffer = lines.pop() ?? ""

                for (const line of lines){
                    if (!line.trim()) continue;

                    try {
                        const parsed = JSON.parse(line) as AggRow
                        set((state) => ({
                            liveData: [...state.liveData!, parsed],
                        }));
                    } catch (err) {
                        set({error: "Failed to parse json"})
                    }
                }
            }

        } catch (err) {
            set({error: "Failed to connect to stream"})
        } finally {
            set({listening: false})
        }

    },
    clearData: () => set({liveData: []})
}))