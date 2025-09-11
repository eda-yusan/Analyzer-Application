@echo off
echo ================================
echo Starting GitHubAnalyzer App...
echo ================================
mvn clean package
java -jar target/github-analyzer-1.0.jar
pause