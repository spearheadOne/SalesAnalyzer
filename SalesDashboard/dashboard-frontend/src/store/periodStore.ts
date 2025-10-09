import {create} from "zustand/react";


export type PeriodUnit = 'm' | 'h' | 'd' | 's'

export interface PeriodStore {

    units: readonly PeriodUnit[],

    period: string,

    setPeriod: (value: number, unit: PeriodUnit) => void,
}


export const usePeriodStore = create<PeriodStore>((set) => ({
    units: ['m', 'h', 'd', 's'] as const,
    period: '1h',

    setPeriod: (value: number, unit: PeriodUnit) => {set({ period: `${value}${unit}`})},

}))
