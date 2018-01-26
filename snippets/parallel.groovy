node {
    
      stage('Build') {
        sh 'java -version'
      }

      stage('test') {
		parallel(
			'JDK 8': {
				node('JDK8') {
          			sh 'java -version'
				}
			},
			'JDK 9': {
				node('JDK9') {
          			sh 'java -version'
				}
			},
			failFast: false
		)    
	  }
}
