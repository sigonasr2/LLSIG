npm i -g git-mob
cat <<-EOF > ~/.git-coauthors
{
  "coauthors": {
    "sig": {
      "name": "sigonasr2",
      "email": "sigonasr2@gmail.com"
    }
  }
}
EOF
git mob sig 
cat <<-EOF > .git/hooks/prepare-commit-msg
#!/usr/bin/env node
let exec = require('child_process').exec,
    fs = require('fs');
const commitMessage = process.argv[2];
// expect .git/COMMIT_EDITMSG
if(/COMMIT_EDITMSG/g.test(commitMessage)){
    let contents = "";
    exec("git mob-print",
      function (err, stdout) {
        if(err) {
            process.exit(0);
        }
        // opens .git/COMMIT_EDITMSG
        contents = fs.readFileSync(commitMessage);
        if(contents.indexOf(stdout.trim()) !== -1) {
            process.exit(0);
        }
        const commentPos = contents.indexOf('# ');
        const gitMessage = contents.slice(0, commentPos);
        const gitComments = contents.slice(commentPos)
        fs.writeFileSync(commitMessage, gitMessage + stdout + gitComments);
        process.exit(0);
    });
}
EOF
chmod +x .git/hooks/prepare-commit-msg
cd LLSIG
echo "Environment is setup!"