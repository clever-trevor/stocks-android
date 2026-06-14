# Stock Tracker

An Android app to track your stock and share portfolio with real-time price updates and home-screen widgets.

## Features

### App
- Add stocks by name, ticker symbol, number of shares held, and unit cost
- Fetches live prices via the [Alpha Vantage](https://www.alphavantage.co/) API
- Shows current price, total value, and profit/loss for each holding
- Pull-to-refresh to update all quotes

### Widgets

**Stock List Widget** — scrollable list of selected holdings showing current price and P&L percentage.

**Holdings Table Widget** — a high-fidelity 4×4 dark-mode portfolio dashboard showing:
- Portfolio total value and overall gain/loss in the header
- Per-holding rows: name, ticker, current price, total value, gain/loss (£ and %)
- Color-coded green/red gain indicators
- "Updated" timestamp and tap-to-refresh button
- Auto-refreshes every 30 minutes via WorkManager when connected to the internet

## Setup

### Prerequisites
- Android Studio
- Android SDK 26+
- An [Alpha Vantage API key](https://www.alphavantage.co/support/#api-key) (free tier available)

### Configuration

Add your API key to `local.properties` (this file is not committed to version control):

```
alpha_vantage_key=YOUR_KEY_HERE
```

### Build

Open the project in Android Studio and run on a device or emulator (API 26+).

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Database | Room (SQLite) |
| Networking | Retrofit + OkHttp |
| Background work | WorkManager |
| Widgets | RemoteViews + RemoteViewsService |
| Architecture | Repository pattern, ViewModel + LiveData |
| Build | AGP 9.x, KSP |

## Project Structure

```
app/src/main/java/com/example/stocktracker/
├── data/
│   ├── local/          # Room database, DAO, StockEntity
│   ├── remote/         # Alpha Vantage API client (Retrofit)
│   └── repository/     # StockRepository (single source of truth)
├── ui/
│   ├── portfolio/      # Main holdings list screen
│   └── addstock/       # Add / edit stock screen
└── widget/
    ├── StockWidgetProvider / StockWidgetFactory   # Stock list widget
    └── HoldingsWidgetProvider / HoldingsWidgetFactory / HoldingsWidgetWorker  # Holdings table widget
```

## Currency

All prices are stored and displayed in GBP (£). The Alpha Vantage API returns prices in the stock's native currency; the app converts to GBP using the exchange rate returned alongside each quote.
