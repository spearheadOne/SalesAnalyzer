import {create} from "zustand/react"
import {STREAM_URL} from "./client.ts"
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { AggRowSchema, type AggRow } from "./schemas";
interface LiveDataStore {

    liveData: AggRow[] | null
    error?: string
    listening: boolean

    startStream: () => Promise<void>
    stopStream: () => void
    clearData: () => void
}


export const useLiveDataStore = create<LiveDataStore>((set, get) => {
    let abortController: AbortController | null = null

    const resetController = () => {
        abortController?.abort()
        abortController = null
    }

    return {
        liveData: [],
        listening: false,
        error: undefined,

        startStream: async () => {
            if (get().listening) return

            resetController()
            abortController = new AbortController()

            set({ listening: true})

            try {
                await fetchEventSource(STREAM_URL, {
                    signal: abortController.signal,

                    onopen: async (response) => {
                        if (!response.ok) {
                            set({ error: `HTTP ${response.status}`, listening: false })
                            resetController()
                            throw new Error(`HTTP ${response.status}`)
                        }
                    },

                    onmessage(event) {
                        if (!event.data) return;

                        try {
                            const json = JSON.parse(event.data);
                            const result = AggRowSchema.safeParse(json);

                            if (!result.success) {
                                set({ error: "Failed to parse message" });
                                return;
                            }

                            const parsed: AggRow = result.data;

                            set((state) => ({
                                liveData: [...(state.liveData ?? []), parsed],
                            }));
                        } catch (e) {
                            set({ error: "Failed to parse message" });
                        }
                    },

                    onerror(err) {
                        if (abortController?.signal.aborted) {
                            return
                        }

                        set({ error: "Stream connection failed", listening: false })
                        resetController()
                        throw err
                    },

                    onclose() {
                        console.log("🔌 Stream closed")
                        resetController()
                        set({ listening: false })
                    },
                })
            } catch (err) {
                if (abortController?.signal.aborted) return

                set((state) => ({
                    listening: false,
                    error: state.error ?? "Stream closed unexpectedly",
                }))
                resetController()
            }
        },

        stopStream: () => {
            if (!get().listening) return
            resetController()
            set({ listening: false })
        },

        clearData: () => set({ liveData: [] }),
    }
})