import {formatCurrency, formatTime} from "../util/util.ts";
import type {TimeSeriesPoint} from "../store/schemas.ts";

type ScatterTooltipProps = {
    point: TimeSeriesPoint
}


export default function ScatterTooltip({point} : ScatterTooltipProps) {
    return (
        <div className="card shadow-sm p-2" style={{ minWidth: 240 }}>
            <div className="fw-semibold mb-1">{formatTime(point.eventTime.toISOString())}</div>
            <div className="small">
                <div className="text-truncate" title={point.productName ?? ''}>
                    <span className="text-muted">Product: </span>{point.productName ?? ''}
                </div>
                <div><span className="text-muted">ID: </span>{point.productId ?? ''}</div>
                <div className="mt-1">
                    <span className="text-muted">Revenue: </span>€{formatCurrency(Number(point.revenue ?? 0))}
                </div>
            </div>
        </div>
    )
}