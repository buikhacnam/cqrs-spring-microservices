## Getting Started

1. Start the required infrastructure:

```bash
docker compose up -d
```

2. Run the microservices:
- Start `account.cmd` (port 4000)

- Start `account.query` (port 4001)

## API Endpoints

### Command API (Port 4000)

- POST `/api/v1/openBankAccount` - Open new account
- PUT `/api/v1/depositFunds/{id}` - Deposit funds
- PUT `/api/v1/withdrawFunds/{id}` - Withdraw funds
- DELETE `/api/v1/closeAccount/{id}` - Close account
- POST `/api/v1/restoreReadDb` - Restore read database

### Query API (Port 4001)

- GET `/api/v1/bankAccountLookup/` - Get all accounts
- GET `/api/v1/bankAccountLookup/byId/{id}` - Get account by ID
- GET `/api/v1/bankAccountLookup/withBalance/{operator}/{amount}` - Search accounts by balance