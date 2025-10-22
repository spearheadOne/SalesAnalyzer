import type {StoreApi} from "zustand/vanilla";
import {useSyncExternalStore} from "react";

export function formatCurrency(n: number){
    return n.toLocaleString(undefined, {maximumFractionDigits: 2})
}

export function useStoreSelector<S, T>(store: StoreApi<S>, selector: (s: S) => T): T {
    return useSyncExternalStore(
        store.subscribe,                 // subscribe
        () => selector(store.getState()),// get snapshot (client)
        () => selector(store.getState()) // get snapshot (SSR)
    )
}