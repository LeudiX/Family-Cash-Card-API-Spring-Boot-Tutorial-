
# Family Cash Card API

## Main Advantages of TDD as  tests to guide the implementation of the API

1.Guide the creation of code in order to arrive at a desired outcome
2.Tests are a powerful safety net to enforce correctness
3.If someone were to make a code change which caused this new test to fail, then I'll have caught the error before it could become an issue

## Security Requirements

The user who created the Cash Card "owns" the Cash Card. Thus, they are the "card owner". Only the card owner can view or update a Cash Card.

### Logic

IF the user is authenticated

... AND they are authorized as a "card owner"

... ... AND they own the requested Cash Card

THEN complete the users's request

BUT don't allow users to access Cash Cards they do not own.
