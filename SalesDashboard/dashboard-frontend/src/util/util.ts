import type {StoreApi} from "zustand/vanilla";
import {useSyncExternalStore} from "react";

export function getCurrency(currency: string) {
    return currency && currency.trim() ? currency : 'EUR';
}

export function formatRevenue(n: number | string, currencyCode: string) {
    const num = typeof n === 'number' ? n : Number(String(n).replace(/[^\d.-]/g, ''));
    if (!Number.isFinite(num)) return '-';

    const hasFraction = Math.abs(num - Math.trunc(num)) >= 0.005;
    const fmt = new Intl.NumberFormat(undefined, {
        style: 'currency',
        currency: currencyCode,
        currencyDisplay: 'symbol',
        minimumFractionDigits: hasFraction ? 2 : 0,
        maximumFractionDigits: hasFraction ? 2 : 0,
    });
    return fmt.format(num);
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

export function parseNumericString (v: unknown): unknown {
    if (typeof v === 'string') {
        return Number(v.replace(/[^\d.-]/g, ''));
    }
    return v;
}