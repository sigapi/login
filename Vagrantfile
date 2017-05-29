Vagrant.configure(2) do |config|

    config.vm.box = "google/gce"

    config.ssh.username = ENV["SSH_USERNAME"]
    config.ssh.private_key_path = ENV["SSH_PRIVATE_KEY_PATH"]

    config.ssh.shell = "bash -c 'BASH_ENV=/etc/profile exec bash'" # https://superuser.com/a/1182104

    config.vm.provision "shell", :path => "vagrant/shell/install.sh"
    config.vm.provision "file", source: "target/login.jar", destination: "/tmp/sigapi/login.jar"
    config.vm.provision "file", source: "secrets.properties", destination: "/tmp/sigapi/secrets.properties"
    config.vm.provision "shell", :path => "vagrant/shell/run.sh"

    config.vm.synced_folder "vagrant", "/vagrant"

    config.vm.provider :google do |google|

        google.name = "login"
        google.image = "ubuntu-1604-xenial-v20170327"
        google.machine_type = "n1-standard-2"
        google.zone = "us-east1-b"
        google.external_ip = "35.185.50.44"
        google.can_ip_forward = true
        google.tags = [
            "http-server"
        ]

    end

end
