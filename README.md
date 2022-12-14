# Project10Scorer_android: ITIS 5280
## UNC Charlotte | Advanced Mobile Application Development

### Members:
- Alex Miller
- Tom Va
- Jared Tamulynas

# The majority of the QR scanning code was learned from a Youtube tutorial, https://www.youtube.com/watch?v=jngzWPWWiKE&t=639s, by Atif Pervaiz. 

## Implementation
Scan QR code to log scorer in. QR code (email: Email) created by admin before hand with scorer's email, first name, last name, etc. <br />
Scan QR code to evaluate a student group. QR code (type: Text) for a group created before hand with the group's name. <br />
Questionnaire screen is one screen. The question changes as the scorer clicks on one of the radio button scores. <br />
After the last question is scored then return to Student Group screen to scan for another student group. <br />

Scorer is authenticated using auth0 via end point /api/login. This end point usees auth0's API to authenticate user. <br />
Scorer is created before hand by administartor via end point /api/signup. <br />

