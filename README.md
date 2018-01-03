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

Execute a sql file
```jenkins
phoenixSql(credentialsId: 'abac12df-4ed1-43df-bc3c-74c8a0936bc0',
    url: 'jdbc:mysql://localhost:3306/test',
    sql: 'test.sql',
    isText: true)
```

```jenkins
withSCM(
    [$class: 'GitSCM',
    additionalCredentials: [],
    excludedCommitMessages: '',
    excludedRegions: '',
    excludedRevprop: '',
    excludedUsers: '',
    filterChangelog: false,
    ignoreDirPropChanges: false,
    includedRegions: '',
    locations: [
        [credentialsId: null,
        depthOption: 'infinity',
        ignoreExternalsOption: true,
        local: '.',
        remote: 'https://github.com/LinuxSuRen/autotest.parent']
    ],
    workspaceUpdater: [$class: 'UpdateUpdater']]
){
    echo 1
}
```