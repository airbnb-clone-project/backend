db.createUser({
    roles: [
        {
            role: "readWrite",
            db: "test"
        }
    ]
});