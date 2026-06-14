# Handoff: Stock Holdings Widget (Android, Dark Mode)

## Overview
A home-screen **Android app widget (4×4 grid size)** that displays the user's stock
holdings as a compact data table. Each row shows a holding's friendly name, ticker,
current share price, original invested amount, current value (the visual hero), and the
gain/loss in both dollars and percent. A header summarizes the whole portfolio.

## About the Design Files
The file in this bundle (`Stock Widget — Option A.dc.html`) is a **design reference
created in HTML** — a prototype showing the intended look and behavior. It is **not
production code to copy directly**.

The task is to **recreate this design inside the Android app's existing environment**.
An Android home-screen widget is built with **RemoteViews** (XML layouts under
`res/layout/`, driven by an `AppWidgetProvider` + `AppWidgetProviderInfo`), or with
**Jetpack Glance** (Compose-style API) if the project already uses Glance/Compose.
Use whichever the codebase already standardizes on. Do not ship the HTML.

> Note: classic RemoteViews widgets support a limited set of views
> (`TextView`, `ImageView`, `LinearLayout`, `FrameLayout`, `GridLayout`, `ListView`,
> etc.). The per-row list is best implemented with a `ListView`/`GridView` backed by a
> `RemoteViewsService` + `RemoteViewsFactory`, or with Glance's `LazyColumn`.

## Fidelity
**High-fidelity (hifi).** Final colors, typography, spacing, and hierarchy are specified
below — recreate it pixel-faithfully using the codebase's tools. Sample numbers are
placeholders; wire real holdings data in.

## Screens / Views

### Widget — Holdings Table
- **Purpose**: Glanceable view of the portfolio. The current value of each holding is the
  emphasized number; gain/loss is color-coded.
- **Layout** (4×4 widget, design reference width ≈ **392 dp**, height grows with rows):
  - Outer container: rounded card, corner radius **30 dp**, padding **24 dp top / 22 dp
    sides / 18 dp bottom**, background `#212125`, 1 dp border `#313137`.
  - **Header row** (space-between): left = title block; right = portfolio totals block.
  - **Column-label row**: 4 columns, a 1 dp bottom divider `#2E2E33`, 9 dp bottom padding.
  - **5 data rows**: same 4-column grid, each with 11 dp vertical padding and a 1 dp bottom
    divider `#29292E`.
  - **Footer row** (space-between): "Updated …" left, "REFRESH ↻" right, 14 dp top margin.
  - **Column widths** are proportional weights: `1.5 / 0.95 / 1.05 / 1.0`
    (Stock / Price / Value / Gain). Columns 2–4 are right-aligned; column 1 left-aligned.
    Inter-column gap **8 dp**.

#### Components

**Header — title block (left)**
- Line 1: `My Holdings` — Sora, 600, **19 sp**, letter-spacing −0.01em, color `#F6F5F1`.
- Line 2: `5 positions` — Sora, 400, **11.5 sp**, color `#76746D`, 2 dp top margin.

**Header — totals block (right, right-aligned)**
- Line 1: portfolio total `$9,070` — IBM Plex Mono, 600, **22 sp**, letter-spacing −0.02em,
  tabular figures, color `#F6F5F1`.
- Line 2: `+$2,470 · +37.4%` — IBM Plex Mono, 500, **12 sp**, color = gain green `#4EC77F`.

**Column labels**
- `STOCK` / `PRICE` / `VALUE` / `GAIN` — Sora, 600, **10 sp**, uppercase,
  letter-spacing 0.09em, color `#62615B`.

**Data row — column 1 (Stock)**
- Name: Sora, 600, **15 sp**, letter-spacing −0.01em, line-height ~1.1, color `#F2F1EC`.
- Ticker: IBM Plex Mono, 500, **10.5 sp**, letter-spacing 0.03em, color `#76746D`.

**Data row — column 2 (Price), right-aligned**
- IBM Plex Mono, 400, **12.5 sp**, tabular figures, color `#B3B1A8`.

**Data row — column 3 (Value), right-aligned**
- Current value (hero): IBM Plex Mono, 600, **16 sp**, letter-spacing −0.02em, tabular
  figures, color `#F6F5F1`.
- Subline `from $1,200`: IBM Plex Mono, 400, **9.5 sp**, tabular figures, color `#62615B`.

**Data row — column 4 (Gain), right-aligned**
- Dollar gain: IBM Plex Mono, 600, **13 sp**, tabular figures, color = `#4EC77F` if gain ≥ 0
  else `#F0795F`.
- Percent: IBM Plex Mono, 500, **10 sp**, tabular figures, same conditional color.
- Use a real minus sign `−` (U+2212) for negatives, e.g. `−$268`, `−17.9%`.

**Footer**
- Left: `Updated 9:41 AM` — IBM Plex Mono, 400, **10 sp**, color `#56554F`.
- Right: `REFRESH ↻` — Sora, 600, **10.5 sp**, uppercase, letter-spacing 0.04em, color
  `#8D8B84`. This is the tap target to refresh quotes.

## Interactions & Behavior
- **Refresh**: tapping the footer "REFRESH" (and ideally the whole widget) triggers a quote
  refresh. In RemoteViews, attach a `PendingIntent` that fires an `ACTION_APPWIDGET_UPDATE`
  / custom action handled by the `AppWidgetProvider`; re-fetch prices, recompute values,
  and call `appWidgetManager.updateAppWidget(...)`. In Glance, use an `actionRunCallback`.
- **Periodic update**: schedule background refresh via `WorkManager` (e.g. every 15–30 min,
  network-permitting) or the `updatePeriodMillis` in `AppWidgetProviderInfo` (min 30 min).
- **Tap to open app** (optional): a `PendingIntent` on the card opens the full holdings
  screen in the app.
- **Color logic**: gain ≥ 0 → green `#4EC77F`; gain < 0 → coral `#F0795F`. Apply to dollar
  gain and percent in column 4, and to the portfolio total subline in the header.
- No hover states (touch surface). Keep tap targets ≥ 48 dp where interactive.

## State / Data
Per holding the widget needs:
- `name` (friendly display name, e.g. "Apple")
- `ticker` (e.g. "AAPL")
- `price` (current share price)
- `initialValue` (original invested amount / cost basis for the position)
- `currentValue` (current market value of the position)
- derived: `gain = currentValue − initialValue`, `gainPct = gain / initialValue`
Portfolio header = sums: `Σ currentValue`, `Σ gain`, `Σ gain / Σ initialValue`.

Data source: your quotes API / repository. The widget reads a cached snapshot (persist via
DataStore/Room) so it renders instantly, then refreshes in the background.

## Design Tokens

### Colors
| Token            | Hex       | Use                                    |
|------------------|-----------|----------------------------------------|
| Surface          | `#212125` | Widget card background                 |
| App background   | `#161618` | Behind the card (preview only)         |
| Border           | `#313137` | Card border                            |
| Divider (header) | `#2E2E33` | Under column labels                    |
| Divider (row)    | `#29292E` | Between data rows                      |
| Text strong      | `#F6F5F1` | Title, values, totals                  |
| Text primary     | `#F2F1EC` | Holding names                          |
| Text secondary   | `#B3B1A8` | Price                                  |
| Text muted       | `#76746D` | Tickers, "5 positions"                 |
| Text faint       | `#62615B` | Column labels, "from $…"               |
| Text faint-2     | `#56554F` | "Updated …"                            |
| Gain (positive)  | `#4EC77F` | Up gain $ / %                          |
| Loss (negative)  | `#F0795F` | Down gain $ / %                        |

### Typography
- Display/UI: **Sora** (400, 500, 600).
- Numerics/mono: **IBM Plex Mono** (400, 500, 600), with **tabular figures** enabled so
  columns align. Bundle both as app fonts (`res/font/`); do not rely on system fonts.
- Sizes (sp): 22 / 19 / 16 / 15 / 13 / 12.5 / 12 / 11.5 / 10.5 / 10 / 9.5 (see components).

### Spacing & shape
- Card radius **30 dp**; card padding 24/22/18 dp.
- Row vertical padding **11 dp**; column gap **8 dp**; header bottom margin 18 dp;
  footer top margin 14 dp.
- Column weights 1.5 / 0.95 / 1.05 / 1.0.
- Card shadow (preview): large soft drop, e.g. y≈24 dp blur≈56 dp at ~70% black + a 1 dp
  inset top highlight at ~4% white. On Android, a subtle elevation/`outline` is enough.

## Assets
- No image assets. The `↻` refresh glyph is the Unicode character U+21BB (or swap for a
  vector drawable refresh icon from the app's icon set).
- Fonts: Sora and IBM Plex Mono (Google Fonts, OFL — safe to bundle).

## Files
- `Stock Widget — Option A.dc.html` — the high-fidelity dark-mode design reference
  (open in a browser to inspect exact rendering).
