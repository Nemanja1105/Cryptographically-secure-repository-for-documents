# Cryptographically-secure-repository-for-documents
A secure repository for storing confidential documents, implemented using the OpenSSL library in the java programming language.

This Github project represents a secure repository for storing confidential documents, implemented using the OpenSSL library.

The application allows for the creation of accounts for multiple users, who log in using a digital certificate. After verifying the validity of the certificate, the user is presented with a form to enter their username and password. Upon successful login, the user has access to a list of their documents.

Each document is divided into N segments, with each segment stored in a different directory to further increase system security and reduce the possibility of document theft. Each segment is encrypted in an appropriate manner to protect the integrity and confidentiality of the documents.

The application allows the user to upload new documents or download existing ones. Any unauthorized modification of stored documents is detected, and the user is notified when attempting to download such documents.

All user certificates are issued by a CA body established before the application's operation, and the CA certificate, CRL list, user certificates, and the currently logged-in user's private key are located at an arbitrary location in the file system.

User certificates are issued for a period of 6 months, and if a user enters incorrect credentials three times during one login, their certificate is automatically suspended. After that, the user has the option of reactivating the certificate (if they enter the correct credentials) or registering a new account.
