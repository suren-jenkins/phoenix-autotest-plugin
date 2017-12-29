# phoenix.plugin

# ftp

upload file to a file server
```jenkins
phoenixFtp(serverHost: 'localhost',
    serverPort: 21,
    username: 'demo',
    password: 'demo',
    srcFile: '/root/pom.xml',
    targetDir: 'demo_demo')
```

copy a file from master to slaver
```jenkins
copy(srcFile: 'changelog0.xml',
    dstFile: 'changelog.xml')
```