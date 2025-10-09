import {create} from "zustand/react";

export type Limit = 1 | 5 | 10 | 20 | 50 | 100;

export interface LimitState {
    limits: readonly Limit[];

    limit: Limit,

    setLimit: (value: Limit) => void,
}

export const useLimitStore = create<LimitState>((set) => ({
    limits: [1, 5, 10, 20, 50, 100] as const,
    limit: 10,

    setLimit: (value: Limit) => {set({ limit: value})},
}))