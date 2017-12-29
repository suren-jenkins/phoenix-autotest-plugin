# phoenix.plugin

# ftp

upload file to a file server
```jenkins
phoenixFtp(serverHost: '192.168.1.1',
    credentialsId: 'a52b4173-cf98-47a0-bd02-3e9019a6082d',
    srcFile: 'pom.xml', targetDir: '.')
```

copy a file from master to slaver
```jenkins
copy(srcFile: 'changelog0.xml',
    dstFile: 'changelog.xml')
```