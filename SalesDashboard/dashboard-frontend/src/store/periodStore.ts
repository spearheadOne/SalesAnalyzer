import {create, useStore} from "zustand/react";
import {createContext, useContext} from "react";
import type {StoreApi} from "zustand/vanilla";


export type PeriodUnit = 'm' | 'h' | 'd' | 's'

export interface PeriodState {

    units: readonly PeriodUnit[],

    period: string,

    setPeriod: (value: number, unit: PeriodUnit) => void,

    getDisplayPeriod: () => string,
}


export function createPeriodStore(): StoreApi<PeriodState> {
    return create<PeriodState>((set, get) => ({
        units: ['m', 'h', 'd', 's'] as const,
        period: '1h',

        setPeriod: (value: number, unit: PeriodUnit) => {
            set({period: `${value}${unit}`})
        },

        getDisplayPeriod(): string {
            const period = get().period;
            const unit = period.slice(-1) as PeriodUnit;
            const value = parseInt(period.slice(0, -1));

            const unitMap: Record<PeriodUnit, string> = {
                'm': 'minute',
                'h': 'hour',
                'd': 'day',
                's': 'second'
            }

            const unitName = unitMap[unit];

            return `${value} ${unitName}${value > 1 ? 's' : ''}`;
        }

    }))
}

export const PeriodStoreContext = createContext<ReturnType<typeof createPeriodStore> | null>(null);

export const usePeriodStore = <T, >(selector: (state: PeriodState) => T): T => {
    const store = useContext(PeriodStoreContext);
    if (!store) throw new Error('Missing PeriodStoreContext.Provider in the tree');
    return useStore(store, selector);
}

