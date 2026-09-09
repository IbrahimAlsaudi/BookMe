# BookMe — Hotel Explorer
 
An Android hotel browsing, favoriting, and booking-simulation app.
## Screenshots

<table>
  <tr>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/home.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/home_no_network.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/filters.png"/></td>
  </tr>
  <tr>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/favorites.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/empty_favorites.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/details.png"/></td>
  </tr>
  <tr>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/booking.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/BookMe/blob/d4b06ff063fc6f361c74b6b84fa4082da6afe6af/Screenshots/booking_confirmed.png"/></td>
  </tr>
</table>


## Tech stack
 
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM + Clean Architecture (presentation / domain / data), with a
  use-case layer between ViewModels and the repository
- **DI:** Hilt
- **Networking:** Retrofit + kotlinx.serialization
- **Local persistence:** Room (also backs favorites — see "Design decisions" below)
- **Async:** Kotlin Coroutines + Flow
- **Images:** Coil
- **Navigation:** Navigation Compose


## Setup
 
1. Clone the repo.
2. Create a free project at [mockapi.io](https://mockapi.io) with a resource named
   `hotels` containing these fields:
   `id` (string), `name` (string), `city` (string), `country` (string), `rating` (number),
   `pricePerNight` (number), `currency` (string), `imageUrl` (string),
   `images` (array), `amenities` (array), `description` (string), `address` (string),
   `latitude` (number), `longitude` (number).
3. Copy `local.properties.example` → `local.properties` and set:
```
   mockapi.base_url=https://YOUR_PROJECT_TOKEN.mockapi.io/
```
4. Open in Android Studio, sync Gradle, run.
`local.properties` is git-ignored — no secrets or personal URLs are committed.


## Architecture
 
```
app/
├── data/
│   ├── local/          → Room: HotelDao, BookMeDatabase, Converters, entity/
│   ├── mapper/         → HotelMappers.kt (Dto↔Entity↔domain)
│   ├── remote/         → HotelApiService (Retrofit), dto/
│   └── repository/     → HotelRepositoryImplementation.kt
├── di/                 → DatabaseModule, NetworkModule, RepositoryModule (Hilt)
├── domain/
│   ├── error/          → AppError, Resource<T>, ThrowableToAppError.kt
│   ├── model/
│   │   ├── booking/    → Booking.kt
│   │   └── hotel/      → Hotel, HotelFilters, HotelQuery
│   ├── repository/     → HotelRepository.kt (interface — the only repository in the app)
│   └── usecase/        → one class per action (GetHotelsUseCase, CreateBookingUseCase, etc.)
└── presentation/
    ├── booking/        → BookingScreen, BookingUiState, BookingViewModel
    ├── components/     → shared composables (HotelCard, ErrorState, FilterBottomSheet, ...)
    ├── favorites/       → FavoritesScreen, FavoritesViewModel
    ├── hoteldetail/     → HotelDetailScreen, HotelDetailUiState, HotelDetailViewModel
    ├── hotellist/       → HotelListScreen, HotelListUiState, HotelListViewModel
    ├── navigation/      → BookMeNavHost, Screen
    └── theme/
```
 


## Key decisions
 
- **mockapi.io** for data instead of a third-party hotel API — full control of the schema, no guessing at an undocumented response shape, still a real network layer (real requests, real failures when offline).
- **Only `city` is filtered server-side** (mockapi only supports exact match). Rating, price range, and name search are filtered client-side, using the same filter logic for both live and cached data.
- **Manual pagination**, not Paging 3 — offline fallback would have needed a full `RemoteMediator`, more than this scope needed.
- **`Resource<T>` + `AppError`** for centralized error handling — one place maps exceptions to `NoInternet` / `Timeout` / `ServerError` / `NotFound` / `Unknown`, so the UI shows the right message per failure type.



## If this were going to production
 
- Replace mockapi.io with a real hotel provider API (versioned contract, proper auth)
- `Double` → `BigDecimal` for all money/price handling
- Sync favorites to a backend account instead of local-only Room
- Paging 3 + `RemoteMediator` instead of manual pagination, for a dataset that won't stay this small
- Network retry with backoff, not just a single try/cache-fallback
- Accessibility pass, localization (language + currency formatting per locale)
- broader test coverage (current tests are a starting point, not full coverage)


## Testing
 
- `HotelRepositoryImplTest` — network success + caching, cache fallback on network failure, search filtering
- `CalculateBookingPriceUseCaseTest` — price/VAT math, zero-nights edge case
- `ValidateBookingDatesUseCaseTest` — missing dates, past check-in, invalid range, room count bounds, valid case
- `FavoritesViewModelTest` — initial state reflects observed favorites, toggling calls the use case
Coverage is focused on the core business logic (booking calculation/validation) and the repository's network/cache behavior, plus one ViewModel — not exhaustive across every screen.




## MVVM instead of MVI
 
I went with MVVM because MVI felt like overkill for this app — wrapping every tap and text change in its own Intent class and reducer just adds extra files without solving a problem I actually had here. With MVVM, each screen still has one clear state object that only the ViewModel can change, which is really the part that matters. Simpler to build and easier to reason about for a project this size.




## AI tool usage

AI tools were a helpful part of my process — I turned to them for brainstorming, working through bugs, spotting gaps in test coverage, reviewing code, and cleaning up documentation. But nothing went in untouched: I reviewed every suggestion myself, adapted what fit, and integrated it by hand. The architecture and implementation choices themselves came down to the actual requirements of the project and the technical constraints I had to work with.
