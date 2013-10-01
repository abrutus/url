from fabric.api import env
from fabric.operations import run, put, local
from fabric.context_managers import settings

env.hosts = ['root@brut.us']
def copy():
	local('./sbt package')
	with settings(warn_only=True):
		run('rm /root/root.war')
	put('target/scala-2.10/brutify_2.10-0.1.0-SNAPSHOT.war', '/root/root.war')
	with settings(warn_only=True):
		run('rm -rf /root/root')
	run('unzip root.war -d root')
	with settings(warn_only=True):
		run('rm -rf /opt/jetty/webapps/root')
	run('mv root /opt/jetty/webapps/')
	run('service jetty restart')
