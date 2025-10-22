import {create, useStore} from "zustand/react";

import {createContext, useContext} from "react";

export type Limit = 1 | 5 | 10 | 20 | 50 | 100;

export interface LimitState {
    limits: readonly Limit[];

    limit: Limit,

    setLimit: (value: Limit) => void,
}

export function createLimitStore() {
    return create<LimitState>((set) => ({
        limits: [1, 5, 10, 20, 50, 100] as const,
        limit: 10,

        setLimit: (value: Limit) => {
            set({limit: value})
        },
    }))
}

export const LimitStoreContext = createContext<ReturnType<typeof createLimitStore> | null>(null);

export const useLimitStore = <T,>(selector: (state: LimitState) => T): T => {
    const store = useContext(LimitStoreContext);
    if (!store) throw new Error('Missing LimitStoreContext.Provider in the tree');
    return useStore(store, selector);
}