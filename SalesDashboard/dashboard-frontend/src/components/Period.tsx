import {type PeriodUnit, usePeriodStore} from "../store/periodStore.ts";

export default function Period() {


    const units = usePeriodStore((s) => s.units);
    const period = usePeriodStore((s) => s.period);
    const setPeriod = usePeriodStore((s) => s.setPeriod);

    const value = parseInt(period, 10) || 1;
    const unit = (period.replace(/\d+/g, '') as PeriodUnit) || 'h';

    const max = 2000
    const min = 1;
    return (
        <div className="d-flex align-items-center gap-2">
            <input type="number"
                   className="form-control form-control-sm w-auto"
                   min={min}
                   max={max}
                   value={value}
                   onChange={(e) => {
                       const raw = e.target.value;
                       if (raw === '') return;

                       const n = parseInt(raw, 10);
                       if (!Number.isFinite(n) ) {
                           setPeriod(1, unit);
                           return;
                       }
                       const clamped = Math.min(Math.max(n, min), max);
                       setPeriod(clamped, unit);
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