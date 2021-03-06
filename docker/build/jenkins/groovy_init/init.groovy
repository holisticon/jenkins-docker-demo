import hudson.model.*
import jenkins.model.*
import org.jenkinsci.plugins.xvfb.Xvfb
import org.jenkinsci.plugins.xvfb.Xvfb.XvfbBuildWrapperDescriptor
import org.jenkinsci.plugins.xvfb.XvfbInstallation

// helper
def e = { filepath ->
  def env = System.getenv()
  evaluate(new File(env["JENKINS_HOME"] + '/init.groovy.d/' + filepath))
}
// imports
def admin_email = e("./../config/AdminEmail.groovy")
def agent_port = e("./../config/AgentPort.groovy")
def chmod = e("./../config/Chmod.groovy")
def csrf = e("./../config/Csrf.groovy")
def extended_email = e("./../config/ExtendedEmail.groovy")
def envvars = e("./../config/Envvars.groovy")
def num_executors = e("./../config/NumExecutors.groovy")
def git = e("./../config/Git.groovy")
def java = e("./../config/Java.groovy")
def matrix_authorization = e("./../config/MatrixAuthorization.groovy")
def maven = e("./../config/Maven.groovy")
def quiet_period = e("./../config/QuietPeriod.groovy")
def user_credential = e("./../config/UserCredentials.groovy")
def ssh_credential = e("./../config/SshCredentials.groovy")
def system_message = e("./../config/SystemMessage.groovy")
def create_ssh_node = e("./../config/Slave.groovy")

// definitions
def env = System.getenv()
def home = env["JENKINS_HOME"]
def j = Jenkins.getInstance()
def creds = '''\
name,global_admin,global_configure_updatecenter,global_read,global_run_scripts,global_upload_plugins,credentials_create,credentials_delete,credentials_manage_domains,credentials_update,credentials_view,agent_build,agent_configure,agent_connect,agent_create,agent_delete,agent_disconnect,job_build,job_cancel,job_configure,job_create,job_delete,job_discover,job_read,job_workspace,run_delete,run_update,view_configure,view_create,view_delete,view_read,scm_tag,metrics_health_check,metrics_thread_dump,metrics_view,job_extendedread,job_move,view_replay
Anonymous,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
authenticated,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
'''+env["JENKINS_ADMIN_USER"]+''',1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
user,0,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
'''


// env
envvars(
    instance    = j,
    env_var_map = [ "LANG": "en_US.UTF-8" ]
)

// configuration
system_message(
    instance = j,
    html = true,
    message = "Welcome to <b>Holistic Jenkins Demo Server</b>"
)

num_executors(
    instance      = j,
    num = env["JENKINS_EXECUTORS"].toInteger()
)

quiet_period(
    instance = j,
    period   = env["JENKINS_QUIET_PERIOD"].toInteger()
)

admin_email(
    instance = j,
    admin_addr= env['JENKINS_ADMIN_ADDR']
)

j.getDescriptorByType(Xvfb.XvfbBuildWrapperDescriptor.class).setInstallations(new XvfbInstallation('default', '/usr/bin', null))

// securing
matrix_authorization(
    instance      = j,
    user_mappings = creds
)

// We trust users: enable CSP for hosting content
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")

csrf(
    instance    = j,
    enable_csrf = true
)

// users
user_credential(
    instance = j,
    username = 'user',
    password = 'password',
    email = 'user@example.com'
)
j.save()

user_credential(
    instance = j,
    username = env["JENKINS_ADMIN_USER"],
    password = env["JENKINS_ADMIN_PASS"],
    email = env["JENKINS_ADMIN_ADDR"]
)
j.save()


// nodes

create_ssh_node(
    instance = j,
    hostname = 'jenkins_node_1',
    sshPrivateKey = '/usr/share/jenkins/.ssh/id_rsa',
    name = 'node1',
    label = '',
    javaHome = '/usr/lib/jvm/java-8-openjdk-amd64',
    home = '/home/jenkins'
)

create_ssh_node(
    instance = j,
    hostname = 'jenkins_node_2',
    sshPrivateKey = '/usr/share/jenkins/.ssh/id_rsa',
    name = 'node2',
    label = '',
    javaHome = '/usr/lib/jvm/java-8-openjdk-amd64',
    home = '/home/jenkins'
)
// tools
git(
    instance = j,
    name  = env['JENKINS_GIT_NAME'],
    email = env['JENKINS_GIT_EMAIL']
)
java(
    instance  = j,
    java_name = "Java",
    java_home = "/docker-java-home"
)

maven(
    instance   = j,
    mavenName = "Maven",
    mavenVersion = "3.3.9"
)

extended_email(
    instance = j
)
j.save()
