import {type PeriodState, type PeriodUnit} from "../store/periodState.ts";

import type {StoreApi, UseBoundStore} from "zustand";

export default function Period({periodStore}: {periodStore: UseBoundStore<StoreApi<PeriodState>>}) {

    const state = periodStore(s => ({
        units: s.units,
        period: s.period,
        setPeriod: s.setPeriod,
    }))
    const { units, period, setPeriod } = state

    const value = parseInt(period, 10) || 1;
    const unit = (period.replace(/\d+/g, '') as PeriodUnit) || 'h';

    return (
        <div className="d-flex align-items-center gap-2">
            <input type="number"
                   className="form-control form-control-sm w-auto"
                   min={1}
                   value={value}
                   onChange={(e) => {
                       const raw = e.target.value;
                       if (raw === '') return; // don't commit while empty
                       const n = parseInt(raw, 10);
                       if (!Number.isFinite(n) || n < 1) {
                           setPeriod(1, unit);
                       } else {
                           setPeriod(n, unit);
                       }
                   }}
            />

            <div className="dropdown">
                <button className="btn btn-secondary btn-sm dropdown-toggle"
                        type="button"
                        id="dropdownMenuButton1"
                        data-bs-toggle="dropdown"
                        aria-haspopup="true"
                        aria-controls="limitMenu"
                        aria-expanded="false">
                    {unit}
                </button>
                <ul className="dropdown-menu" aria-labelledby="limitDropdown" role="menu">
                    {units.map((u) => (
                        <li key={u}>
                            <button type="button" className="dropdown-item" role="menuitem"
                                    onClick={() => setPeriod(value, u)}>
                                {u}
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    )
}