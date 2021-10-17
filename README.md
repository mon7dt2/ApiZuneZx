# ApiZuneZx

Spring boot rest api.

## Getting Started
- Port server: 8500.
- Use mysql in xampp.
API Examples: 
- GET Get all orders: http://localhost:8500/api/order/orders/
- POST Register for customer: http://localhost:8500/api/auths/customers/register 
  - Authorization Basic + Base64Encode(username:password), Example: Basic dHVuZzFAZ21haWwuY29tOlR1bmcxMjM0NQ==
  - Body Register:
{
  "address": "string",
  "email": "string",
  "fullName": "string",
  "gender": "string",
  "phone": "string"
}
## Authors
- Vũ Quang Tùng: https://github.com/mon7dt2
- Phạm Việt Phong: https://github.com/PvP100
