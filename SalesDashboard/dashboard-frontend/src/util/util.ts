import type {StoreApi} from "zustand/vanilla";
import {useSyncExternalStore} from "react";

export function formatCurrency(n: number){
    return n.toLocaleString(undefined, {maximumFractionDigits: 2})
}

export function formatTime(iso: string) {
    const d = new Date(iso);
    const now = new Date();
    const sameDay = d.toDateString() === now.toDateString();
    return sameDay
        ? d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        : d.toLocaleString([], {
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
        });
}

export function useStoreSelector<S, T>(store: StoreApi<S>, selector: (s: S) => T): T {
    return useSyncExternalStore(
        store.subscribe,                 // subscribe
        () => selector(store.getState()),// get snapshot (client)
        () => selector(store.getState()) // get snapshot (SSR)
    )
}